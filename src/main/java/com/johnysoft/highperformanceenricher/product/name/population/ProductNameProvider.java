package com.johnysoft.highperformanceenricher.product.name.population;

import java.util.Optional;

public interface ProductNameProvider {
    Optional<String> getProductName(Integer productId);
}
