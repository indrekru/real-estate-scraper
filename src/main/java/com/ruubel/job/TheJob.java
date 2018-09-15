package com.ruubel.job;

import com.ruubel.model.Property;
import com.ruubel.model.PropertySource;
import com.ruubel.service.MailService;
import com.ruubel.service.PropertyService;
import com.ruubel.util.ScraperUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component
public class TheJob {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";
    private final static String url = "https://kinnisvaraportaal-kv-ee.postimees.ee/?act=search.simple&last_deal_type=1&company_id=&page=1&orderby=cdwl&page_size=50&deal_type=1&dt_select=1&county=1&search_type=new&parish=1061&rooms_min=&rooms_max=&price_min=&price_max=&nr_of_people=&area_min=&area_max=&floor_min=&floor_max=&energy_certs=&keyword=";

    private PropertyService propertyService;
    private MailService mailService;

    // Center of Tallinn
    private Double latitudeTln = 59.43696079999999;
    private Double longitudTln = 24.753574699999945;

    @Autowired
    public TheJob(PropertyService propertyService, MailService mailService) {
        this.propertyService = propertyService;
        this.mailService = mailService;
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

                Double pricePerSqm = price / area;

                String propertyUrl = String.format("http://www.kv.ee/%s", externalId);

                // Grading
                int points = 0;
                if (price < 65000) {
                    points++;
                }

                if (pricePerSqm < 1500){
                    points++;
                }

                if (area > 30) {
                    points++;
                }

                if (rooms >= 1) {
                    points++;
                }

                if (floor > 1) {
                    points++;
                }

                Double distance, latitude = null, longitude = null;

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

                    distance = ScraperUtils.distance(latitudeTln, latitude, longitudTln, longitude);

                    if (distance < 3500) {
                        points++;
                    }
                }

                dbProperty = new Property(PropertySource.KV, externalId, title, rooms, price, floor, area, latitude, longitude, false);
                propertyService.save(dbProperty);

                if (points > 5) {
                    log.info("Notifying : " + propertyUrl);
                    mailService.notifyQualifiedProperty(propertyUrl);
                    dbProperty.setNotified(true);
                    propertyService.save(dbProperty);
                }

            }
            log.info("Done...");

        } catch (Exception e) {
            e.printStackTrace();
            mailService.notifyCrash();
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
