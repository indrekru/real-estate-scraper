package com.ruubel.job;

import com.ruubel.model.Property;
import com.ruubel.model.ScrapeSource;
import com.ruubel.service.GradingService;
import com.ruubel.service.property.IPropertyService;
import com.ruubel.service.MailingService;
import com.ruubel.util.ScraperUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

@Component
public class ScrapeJob {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";

    @Value("${scraping.url}")
    private String url;

    private IPropertyService propertyService;
    private MailingService mailingService;
    private GradingService gradingService;
    private SSLSocketFactory sslSocketFactory;

    @Autowired
    public ScrapeJob(IPropertyService propertyService, MailingService mailingService, GradingService gradingService) {
        this.propertyService = propertyService;
        this.mailingService = mailingService;
        this.gradingService = gradingService;
        this.sslSocketFactory = socketFactory();
    }

    @Scheduled(cron = "0 0/20 * * * ?") // Every 20 minutes
    public void run() {
        log.info("Running job");
        String rawDocument = getDocument(url);
        Document document = Jsoup.parse(rawDocument);
        try {

            Elements allProperties = document.select("tr.object-type-apartment.object-item");

            for (Element property : allProperties) {
                // Skip ad
                boolean isAd = property.hasClass("hide-on-tablet");
                if (isAd) {
                    continue;
                }

                String externalId = property.attr("id");

                Property dbProperty = propertyService.findByExternalId(externalId);
                if (dbProperty != null) {
                    log.info("Skip, scraped");
                    continue;
                }

                String title = ScraperUtils.extractText(property, "h2.object-title");
                Long price = ScraperUtils.extractLong(property, "p.object-price-value");
                Integer rooms = ScraperUtils.extractInt(property, "td.object-rooms");
                Double area = ScraperUtils.extractDouble(property, "td.object-m2");

                if (ScraperUtils.isAnyNull(price, rooms,area)) {
                    continue;
                }

                Element descriptionElement = property.select("p.object-excerpt").get(0);
                String descriptionText = descriptionElement.text();
                Integer floor = 0; // Shittiest floor ever
                try {
                    String floorStrWar = descriptionText.substring(descriptionText.indexOf("Korrus ") + 7, descriptionText.indexOf(","));
                    if (floorStrWar.contains("/")) {
                        String[] floors = floorStrWar.split("/");
                        floor = Integer.parseInt(floors[0]);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                String propertyUrl = String.format("http://www.kv.ee/%s", externalId);

                Double latitude = null, longitude = null;

                dbProperty = new Property(ScrapeSource.KV, externalId, title, rooms, price, floor, area, latitude, longitude, false);

                // Grading, max points 6
                int points = gradingService.calculatePreliminaryPoints(dbProperty);

                if (points >= 5){
                    // Interesting, fetch location
                    log.info("Fetching location...");

                    String rawPropertyDocument = getDocument(propertyUrl);
                    Document propertyDocument = Jsoup.parse(rawPropertyDocument);

                    if (propertyDocument != null) {

                        if (rawPropertyDocument.contains("puitmaja")
                                || rawPropertyDocument.contains("palkmaja")) {
                            // Wooden buildings get a penalty
                            points--;
                        } else if (rawPropertyDocument.contains("kivimaja")
                                || rawPropertyDocument.contains("paneelmaja")) {
                            // Rock buildings get praised
                            points++;
                        }

                        Elements mapImgs = propertyDocument.select("a.gtm-object-map");
                        if (mapImgs.size() > 0) {
                            Element mapImg = mapImgs.get(0);
                            String imgUrl = mapImg.attr("href");
                            String coords = imgUrl.substring(imgUrl.indexOf("query=") + 6);
                            String[] coordsArr = coords.split(",");

                            latitude = Double.parseDouble(coordsArr[0]);
                            longitude = Double.parseDouble(coordsArr[1]);

                            dbProperty.setLatitude(latitude);
                            dbProperty.setLongitude(longitude);

                            points += gradingService.calculateDistancePoints(dbProperty);
                        }
                    }
                }

                propertyService.save(dbProperty);

                // Max points 8
                if (points >= 7) {
                    log.info("Notifying : " + propertyUrl + ", score: " + points);
                    mailingService.notifyQualifiedProperty(propertyUrl, points);
                    dbProperty.setNotified(true);
                    propertyService.save(dbProperty);
                }

            }
            log.info("Done...");

        } catch (Exception e) {
            e.printStackTrace();
            mailingService.notifyCrash();
        }
    }

    private String getDocument(String url) {
        Connection connection = Jsoup.connect(url);
        connection.sslSocketFactory(sslSocketFactory);
        connection.timeout(60000);
        connection.method(Connection.Method.GET);
        connection.userAgent(userAgent);
        connection.cookies(new HashMap<>());
        try {
            Connection.Response response = connection.execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }

}
