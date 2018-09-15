package com.ruubel.service;

import com.ruubel.model.Property;
import com.ruubel.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

abstract class AbstractPropertyService implements IPropertyService {

    protected PropertyRepository propertyRepository;

    @Autowired
    public AbstractPropertyService(PropertyRepository propertyRepository) {
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

    public List<Property> findAllTop100ByOrderByDateCreatedDesc() {
        return propertyRepository.findAllTop100ByOrderByDateCreatedDesc();
    }
}
