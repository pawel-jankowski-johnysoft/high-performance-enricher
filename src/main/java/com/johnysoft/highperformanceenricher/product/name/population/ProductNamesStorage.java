package com.johnysoft.highperformanceenricher.product.name.population;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class ProductNamesStorage implements ProductNameProvider, ProductNamesPopulator {
    private final Map<Integer, String> productToNameMappings = new HashMap<>();
    private final ProductsNamesParser parser;

    @Override
    public Optional<String> getProductName(Integer productId) {
        return Optional.ofNullable(productId).map(productToNameMappings::get);
    }

    @Override
    public void populate(Path path) {
        parser.parse(path)
                .forEach(it -> productToNameMappings.put(it.productId(), it.productName()));
    }
}
