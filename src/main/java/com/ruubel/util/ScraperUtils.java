package com.ruubel.util;

import org.jsoup.nodes.Element;

public class ScraperUtils {

    public static Long extractLong (Element parent, String cssSelector) {
        String text = extractText(parent, cssSelector);
        if (text.length() > 0) {
            return Long.parseLong(cleanNumberString(text));
        }
        return null;
    }

    public static Integer extractInt (Element parent, String cssSelector) {
        String text = extractText(parent, cssSelector);
        if (text.length() > 0) {
            return Integer.parseInt(cleanNumberString(text));
        }
        return null;
    }

    public static Double extractDouble (Element parent, String cssSelector) {
        String text = extractText(parent, cssSelector);
        if (text.length() > 0) {
            return Double.parseDouble(cleanNumberString(text));
        }
        return null;
    }

    public static String extractText(Element parent, String cssSelector) {
        Element element = parent.select(cssSelector).get(0);
        return element.text().trim();
    }

    public static String cleanNumberString(String source) {
        return source.replaceAll("[^\\d.]", "");
    }

    public static boolean isAnyNull (Object ... elements) {
        for (Object element : elements) {
            if (element == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds distance between two coordinates in meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                            double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2) + Math.pow(0, 2);

        return Math.sqrt(distance);
    }

}
