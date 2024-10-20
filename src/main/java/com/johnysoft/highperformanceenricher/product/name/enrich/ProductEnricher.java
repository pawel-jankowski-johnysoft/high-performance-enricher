package com.johnysoft.highperformanceenricher.product.name.enrich;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
class ProductEnricher {

    private final ProductParser parser;
    private final EnrichProductsProcessorFactory enrichProductsProcessorFactory;

    @SneakyThrows
    public InputStream enrich(InputStream csvContent) {
        try (var processor = enrichProductsProcessorFactory.build()) {
            parser.parse(csvContent, processor::process);
            processor.complete();
            return Files.newInputStream(processor.getProcessedProducts());
        }
    }
}
