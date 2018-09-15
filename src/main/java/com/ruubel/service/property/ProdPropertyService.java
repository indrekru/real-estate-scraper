package com.ruubel.service.property;

import com.ruubel.repository.PropertyRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("default")
public class ProdPropertyService extends AbstractPropertyService {
    public ProdPropertyService(PropertyRepository propertyRepository) {
        super(propertyRepository);
    }
}
