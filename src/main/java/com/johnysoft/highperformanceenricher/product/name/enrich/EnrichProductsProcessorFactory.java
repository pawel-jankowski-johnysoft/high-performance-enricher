package com.johnysoft.highperformanceenricher.product.name.enrich;

import com.johnysoft.highperformanceenricher.product.name.population.ProductNameProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class EnrichProductsProcessorFactory {

    private final ProductNameProvider productNameProvider;
    private final int bufferSize;

    EnrichProductsProcessorFactory(ProductNameProvider productNameProvider, @Value("${products.buffer.size:100000}") int bufferSize) {
        this.productNameProvider = productNameProvider;
        this.bufferSize = bufferSize;
    }

    public EnrichProductsProcessor build() {
        return EnrichProductsProcessor.builder()
                .productNameProvider(productNameProvider)
                .bufferSize(bufferSize)
                .temporaryStorage(ProductsTemporaryCSVStorage.create())
                .build();
    }
}
