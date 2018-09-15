package com.ruubel.controller;

import com.ruubel.model.Property;
import com.ruubel.service.GradingService;
import com.ruubel.service.IPropertyService;
import com.ruubel.util.ScraperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/property")
public class PropertyController {

    private IPropertyService propertyService;
    private GradingService gradingService;

    @Autowired
    public PropertyController(IPropertyService propertyService, GradingService gradingService) {
        this.propertyService = propertyService;
        this.gradingService = gradingService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getProperties() {
        List<Property> properties = propertyService.findAllTop100ByOrderByDateCreatedDesc();

        List<Map<String, Object>> out = new ArrayList<>();

        for (Property property : properties) {
            out.add(new HashMap<String, Object>(){{
                put("scraped", property.getDateCreated());
                put("rooms", property.getRooms());
                put("price", property.getPrice());
                put("area", property.getArea());
                put("floor", property.getFloor());
                put("title", property.getTitle());
                if (property.getLongitude() != null) {
                    put("distance", ScraperUtils.distance(property.getLatitude(), GradingService.LATITUDE_TLN, property.getLongitude(), GradingService.LONGITUDE_TLN));
                }
                put("points", gradingService.calculatePoints(property));
                put("url", String.format("http://www.kv.ee/%s", property.getExternalId()));
                put("notified", property.getNotified());
            }});
        }

        return new ResponseEntity<>(out, HttpStatus.OK);
    }

}
