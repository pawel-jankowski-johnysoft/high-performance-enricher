package com.johnysoft.highperformanceenricher.configuration;

import com.johnysoft.highperformanceenricher.product.name.population.ProductNamesPopulator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
class ProductPopulator implements ApplicationRunner {
    private final ProductNamesPopulator populator;
    private final Resource resource;

    ProductPopulator(ProductNamesPopulator populator, @Value("classpath:product_name_mappings.csv") Resource resource) {
        this.populator = populator;
        this.resource = resource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        populator.populate(Paths.get(resource.getURI()));
    }
}
