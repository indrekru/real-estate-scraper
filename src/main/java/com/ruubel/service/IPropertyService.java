package com.ruubel.service;

import com.ruubel.model.Property;

import java.time.Instant;
import java.util.List;

public interface IPropertyService {
    void save(Property property);
    Property findByExternalId(String externalId);
    List<Property> findByDateCreatedLessThan(Instant date);
    void delete(Property property);
    List<Property> findAllTop100ByOrderByDateCreatedDesc();
}
