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
    public final static long MAX_PRICE     = 40000;
    public final static int MAX_PRICE_SQM  = 3000;
    public final static double MIN_AREA    = 10.0;
    public final static int MIN_ROOMS      = 1;
    public final static int MIN_FLOOR      = 1;
    public final static int MAX_FLOOR      = 5;
    public final static int DIST_METERS    = 2500;

    public final static String[] encouragingWords = new String[]{
        "kesklinn"
    };
    public final static String[] discouragingWords = new String[]{
        "lasnamägi",
        "lasnamäe",
        "nõmme",
        "õismägi",
        "õismäe"
    };

    // Max points possible 6
    public int calculatePreliminaryPoints(Property property) {
        int points = 0;
        if (property.getPrice() <= MAX_PRICE) {
            points++;
        }

        String title = property.getTitle().toLowerCase();
        for (String encouragingWord : encouragingWords) {
            if (title.contains(encouragingWord)) {
                points++;
                break;
            }
        }

        for (String discouragingWord : discouragingWords) {
            if (title.contains(discouragingWord)) {
                points--;
                break;
            }
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
        if (distance <= DIST_METERS) {
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
