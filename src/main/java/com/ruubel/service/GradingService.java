package com.ruubel.service;

import com.ruubel.model.Property;
import com.ruubel.util.ScraperUtils;
import org.springframework.stereotype.Service;

@Service
public class GradingService {

    // Center of Tallinn
    public static Double LATITUDE_TLN = 59.43696079999999;
    public static Double LONGITUDE_TLN = 24.753574699999945;

    // CONFIG:
    public final long MAX_PRICE = 70000;
    public final int MAX_PRICE_SQM = 1900;
    public final double MIN_AREA = 30.0;
    public final int MIN_ROOMS = 1;
    public final int MIN_FLOOR = 1;
    public final int MAX_FLOOR = 5;
    public final int MAX_DISTANCE_FROM_CENTER_METERS = 2500;

    public int calculatePreliminaryPoints(Property property) {
        int points = 0;
        if (property.getPrice() <= MAX_PRICE) {
            points++;
        }

        Double pricePerSqm = property.getPrice() / property.getArea();

        if (pricePerSqm <= MAX_PRICE_SQM){
            points++;
        }

        if (property.getArea() >= MIN_AREA) {
            points++;
        }

        if (property.getRooms() >= MIN_ROOMS) {
            points++;
        }

        if (property.getFloor() >= MIN_FLOOR && property.getFloor() <= MAX_FLOOR) {
            points++;
        }
        return points;
    }

    public int calculateDistancePoints(Property property) {
        if (property.getLatitude() == null) {
            return 0;
        }
        Double distance = ScraperUtils.distance(LATITUDE_TLN, property.getLatitude(), LONGITUDE_TLN, property.getLongitude());
        if (distance <= MAX_DISTANCE_FROM_CENTER_METERS) {
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
