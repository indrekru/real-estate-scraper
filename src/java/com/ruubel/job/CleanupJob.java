package com.ruubel.job;

import com.ruubel.model.Property;
import com.ruubel.model.PropertySource;
import com.ruubel.service.MailService;
import com.ruubel.service.PropertyService;
import com.ruubel.util.ScraperUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class CleanupJob {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Cleans up the DB from older entries, otherwise this might NOT go on forever...
     */

    private PropertyService propertyService;

    @Autowired
    public CleanupJob(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @Scheduled(cron = "0 55 23 * * ?") // Every 23.55 midnight
    public void run() {
        log.info("Cleanup started...");

        Instant date = Instant.now();
        date = date.minusSeconds(432000); // 5 days

        List<Property> oldProperties = propertyService.findByDateCreatedLessThan(date);
        for (Property oldProperty : oldProperties) {
            propertyService.delete(oldProperty);
        }

        log.info(String.format("Cleanup finished, removed %s items", oldProperties.size()));
    }

}
