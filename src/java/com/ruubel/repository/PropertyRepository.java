package com.ruubel.repository;

import com.ruubel.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID> {
    Property findByExternalId(String externalId);
    List<Property> findByDateCreatedLessThan(Instant date);
}
