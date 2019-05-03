package com.ruubel.service;

import com.ruubel.model.Property;
import com.ruubel.model.ScrapeSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class GradingServiceSpec {

    private GradingService gradingService;

    public GradingServiceSpec() {
        gradingService = new GradingService();
    }

    @Test
    public void whenNoCriteriaMatches_then0PointsGiven(){
        Property property = new Property(ScrapeSource.KV, "122", "title",
                gradingService.MIN_ROOMS - 1,
                gradingService.MAX_PRICE + 1,
                gradingService.MIN_FLOOR - 1,
                gradingService.MIN_AREA - 1.0,
                52.4534, 29.232,
                false);

        int points = gradingService.calculatePreliminaryPoints(property);

        assertEquals(0, points);
    }

    @Test
    public void whenRoomsCriteriaMatches_then1PointsGiven(){
        Property property = new Property(ScrapeSource.KV, "122", "title",
                gradingService.MIN_ROOMS,
                gradingService.MAX_PRICE + 1,
                gradingService.MIN_FLOOR - 1,
                gradingService.MIN_AREA - 1.0,
                52.4534, 29.232,
                false);

        int points = gradingService.calculatePreliminaryPoints(property);

        assertEquals(1, points);
    }

    @Test
    public void whenRoomsAndPriceCriteriaMatches_then2PointsGiven(){
        Property property = new Property(ScrapeSource.KV, "122", "title",
                gradingService.MIN_ROOMS,
                gradingService.MAX_PRICE,
                gradingService.MIN_FLOOR - 1,
                gradingService.MIN_AREA - 1.0,
                52.4534, 29.232,
                false);

        int points = gradingService.calculatePreliminaryPoints(property);

        assertEquals(2, points);
    }

    @Test
    public void whenRoomsAndPriceAndFloorCriteriaMatches_then3PointsGiven(){
        Property property = new Property(ScrapeSource.KV, "122", "title",
                gradingService.MIN_ROOMS,
                gradingService.MAX_PRICE,
                gradingService.MIN_FLOOR,
                gradingService.MIN_AREA - 1.0,
                52.4534, 29.232,
                false);

        int points = gradingService.calculatePreliminaryPoints(property);

        assertEquals(3, points);
    }

    @Test
    public void whenRoomsAndPriceAndFloorAndAreaCriteriaMatches_then4PointsGiven(){
        Property property = new Property(ScrapeSource.KV, "122", "title",
                gradingService.MIN_ROOMS,
                gradingService.MAX_PRICE,
                gradingService.MIN_FLOOR,
                gradingService.MIN_AREA,
                52.4534, 29.232,
                false);

        int points = gradingService.calculatePreliminaryPoints(property);

        assertEquals(4, points);
    }

    @Test
    public void whenRoomsAndPriceAndFloorAndAreaAndPriceSqMeterCriteriaMatches_then5PointsGiven(){
        Property property = new Property(ScrapeSource.KV, "122", "title",
                gradingService.MIN_ROOMS,
                gradingService.MAX_PRICE - 30000,
                gradingService.MIN_FLOOR,
                gradingService.MIN_AREA,
                52.4534, 29.232,
                false);

        int points = gradingService.calculatePreliminaryPoints(property);

        assertEquals(5, points);
    }

}
