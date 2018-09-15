package com.ruubel.service.property;

import com.ruubel.model.Property;
import com.ruubel.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class DevPropertyService extends AbstractPropertyService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public DevPropertyService(PropertyRepository propertyRepository) {
        super(propertyRepository);
    }

    public void save(Property property) {
    }

    @Override
    public void delete(Property property) {
    }
}
