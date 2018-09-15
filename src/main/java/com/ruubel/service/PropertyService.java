package com.ruubel.service;

import com.ruubel.model.Property;
import com.ruubel.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class PropertyService {

    private PropertyRepository propertyRepository;

    @Autowired
    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public Property findByExternalId(String externalId) {
        return propertyRepository.findByExternalId(externalId);
    }

    public void save(Property property) {
        propertyRepository.save(property);
    }

    public List<Property> findByDateCreatedLessThan(Instant date) {
        return propertyRepository.findByDateCreatedLessThan(date);
    }

    public void delete(Property property) {
        propertyRepository.delete(property);
    }
}
