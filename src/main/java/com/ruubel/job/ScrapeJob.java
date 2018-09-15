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

import java.io.IOException;
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

    @Autowired
    public ScrapeJob(IPropertyService propertyService, MailingService mailingService, GradingService gradingService) {
        this.propertyService = propertyService;
        this.mailingService = mailingService;
        this.gradingService = gradingService;
    }

    @Scheduled(cron = "0 0/20 * * * ?") // Every 20 minutes
    public void run() {
        log.info("Running job");
        Document document = getDocument(url);
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
                String floorStrWar = descriptionText.substring(descriptionText.indexOf("Korrus ") + 7, descriptionText.indexOf(","));

                Integer floor = 1; // Shittiest floor ever
                if (floorStrWar.contains("/")) {
                    String[] floors = floorStrWar.split("/");
                    floor = Integer.parseInt(floors[0]);
                }

                String propertyUrl = String.format("http://www.kv.ee/%s", externalId);

                Double latitude = null, longitude = null;

                dbProperty = new Property(ScrapeSource.KV, externalId, title, rooms, price, floor, area, latitude, longitude, false);

                // Grading
                int points = gradingService.calculatePreliminaryPoints(dbProperty);

                if (points > 4){
                    // Interesting, fetch location
                    log.info("Fetching location...");

                    Document propertyDocument = getDocument(propertyUrl);

                    Element mapImg = propertyDocument.select("a.gtm-object-map").get(0);
                    String imgUrl = mapImg.attr("href");
                    String coords = imgUrl.substring(imgUrl.indexOf("query=") + 6, imgUrl.length());
                    String[] coordsArr = coords.split(",");

                    latitude = Double.parseDouble(coordsArr[0]);
                    longitude = Double.parseDouble(coordsArr[1]);

                    dbProperty.setLatitude(latitude);
                    dbProperty.setLongitude(longitude);

                    points += gradingService.calculateDistancePoints(dbProperty);
                }

                propertyService.save(dbProperty);

                if (points > 4) {
                    log.info("Notifying : " + propertyUrl);
                    mailingService.notifyQualifiedProperty(propertyUrl);
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

    private Document getDocument(String url) {
        Connection connection = Jsoup.connect(url);
        connection.timeout(6000);
        connection.method(Connection.Method.GET);
        connection.userAgent(userAgent);
        connection.cookies(new HashMap<>());
        try {
            Document document = connection.execute().parse();
            return document;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
