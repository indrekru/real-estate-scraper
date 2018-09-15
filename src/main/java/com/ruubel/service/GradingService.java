package com.ruubel.service;

import com.ruubel.model.Property;
import com.ruubel.util.ScraperUtils;
import org.springframework.stereotype.Service;

@Service
public class GradingService {

    // Center of Tallinn
    public static Double LATITUDE_TLN = 59.43696079999999;
    public static Double LONGITUDE_TLN = 24.753574699999945;

    public int calculatePreliminaryPoints(Property property) {
        // Grading
        int points = 0;
        if (property.getPrice() < 65000) {
            points++;
        }

        Double pricePerSqm = property.getPrice() / property.getArea();

        if (pricePerSqm < 1500){
            points++;
        }

        if (property.getArea() > 30) {
            points++;
        }

        if (property.getRooms() >= 1) {
            points++;
        }

        if (property.getFloor() > 1) {
            points++;
        }
        return points;
    }

    public int calculateDistancePoints(Property property) {
        if (property.getLatitude() == null) {
            return 0;
        }
        Double distance = ScraperUtils.distance(LATITUDE_TLN, property.getLatitude(), LONGITUDE_TLN, property.getLongitude());
        if (distance < 3500) {
            return 1;
        }
        return 0;
    }

    public int calculatePoints(Property property) {
        int points = calculatePreliminaryPoints(property);
        points += calculateDistancePoints(property);
        return points;
    }
}
