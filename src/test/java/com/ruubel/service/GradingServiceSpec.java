package com.ruubel.service;

import com.ruubel.model.Property;
import com.ruubel.model.ScrapeSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static com.ruubel.service.GradingService.*;

@RunWith(SpringRunner.class)
public class GradingServiceSpec {

    private GradingService gradingService;

    public GradingServiceSpec() {
        gradingService = new GradingService();
    }

    @Test
    public void whenNoCriteriaMatches_then0PointsGiven(){
        Property property = new Property(ScrapeSource.KV, "122", "title",
                MIN_ROOMS - 1,
                MAX_PRICE + 1,
                MIN_FLOOR - 1,
                MIN_AREA - 1.0,
                52.4534, 29.232,
                false);

        int points = gradingService.calculatePreliminaryPoints(property);

        assertEquals(0, points);
    }

    @Test
    public void whenRoomsCriteriaMatches_then1PointsGiven(){
        Property property = new Property(ScrapeSource.KV, "122", "title",
                MIN_ROOMS,
                MAX_PRICE + 1,
                MIN_FLOOR - 1,
                MIN_AREA - 1.0,
                52.4534, 29.232,
                false);

        int points = gradingService.calculatePreliminaryPoints(property);

        assertEquals(1, points);
    }

    @Test
    public void whenRoomsAndPriceCriteriaMatches_then2PointsGiven(){
        Property property = new Property(ScrapeSource.KV, "122", "title",
                MIN_ROOMS,
                MAX_PRICE,
                MIN_FLOOR - 1,
                MIN_AREA - 1.0,
                52.4534, 29.232,
                false);

        int points = gradingService.calculatePreliminaryPoints(property);

        assertEquals(2, points);
    }

    @Test
    public void whenRoomsAndPriceAndFloorCriteriaMatches_then3PointsGiven(){
        Property property = new Property(ScrapeSource.KV, "122", "title",
                MIN_ROOMS,
                MAX_PRICE,
                MIN_FLOOR,
                MIN_AREA - 1.0,
                52.4534, 29.232,
                false);

        int points = gradingService.calculatePreliminaryPoints(property);

        assertEquals(3, points);
    }

    @Test
    public void whenRoomsAndPriceAndFloorAndAreaCriteriaMatches_then4PointsGiven(){
        Property property = new Property(ScrapeSource.KV, "122", "title",
                MIN_ROOMS,
                MAX_PRICE,
                MIN_FLOOR,
                MIN_AREA,
                52.4534, 29.232,
                false);

        int points = gradingService.calculatePreliminaryPoints(property);

        assertEquals(4, points);
    }

    @Test
    public void whenRoomsAndPriceAndFloorAndAreaAndPriceSqMeterCriteriaMatches_then5PointsGiven(){
        Property property = new Property(ScrapeSource.KV, "122", "title",
                MIN_ROOMS,
                MAX_PRICE - 50000,
                MIN_FLOOR,
                MIN_AREA,
                52.4534, 29.232,
                false);

        int points = gradingService.calculatePreliminaryPoints(property);

        assertEquals(5, points);
    }

    @Test
    public void whenDiscouragingWordAppears_thenDecreasesPoint(){
        Property property = new Property(ScrapeSource.KV, "122", "dsfgdfghdhlasnamägisdghfhf",
                MIN_ROOMS - 1,
                MAX_PRICE + 1,
                MIN_FLOOR - 1,
                MIN_AREA - 1.0,
                52.4534, 29.232,
                false);

        int points = gradingService.calculatePreliminaryPoints(property);

        assertEquals(-1, points);
    }

    @Test
    public void whenEncouragingWordAppears_thenDIncreasesPoint(){
        Property property = new Property(ScrapeSource.KV, "122", "dsfgdfghdhkesklinnsdghfhf",
                MIN_ROOMS - 1,
                MAX_PRICE + 1,
                MIN_FLOOR - 1,
                MIN_AREA - 1.0,
                52.4534, 29.232,
                false);

        int points = gradingService.calculatePreliminaryPoints(property);

        assertEquals(1, points);
    }

    @Test
    public void whenDiscouragingAndEncouragingWordsAppears_thenNoPointChange(){
        Property property = new Property(ScrapeSource.KV, "122", "dsfgdflasnamägighdhkesklinnsdghfhf",
                MIN_ROOMS - 1,
                MAX_PRICE + 1,
                MIN_FLOOR - 1,
                MIN_AREA - 1.0,
                52.4534, 29.232,
                false);

        int points = gradingService.calculatePreliminaryPoints(property);

        assertEquals(0, points);
    }

    @Test
    public void whenAllMatches_thenNReturnsMaxPoints(){
        Property property = new Property(ScrapeSource.KV, "122", "dsfgdfkesklinnsdghfhf",
                MIN_ROOMS,
                MAX_PRICE - 40000,
                MIN_FLOOR,
                MIN_AREA,
                52.4534, 29.232,
                false);

        int points = gradingService.calculatePreliminaryPoints(property);

        assertEquals(6, points);
    }

}
