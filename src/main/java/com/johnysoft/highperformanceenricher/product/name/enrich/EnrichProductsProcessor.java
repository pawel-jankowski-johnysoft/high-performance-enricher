package com.johnysoft.highperformanceenricher.product.name.enrich;

import com.johnysoft.highperformanceenricher.product.name.population.ProductNameProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Builder
@RequiredArgsConstructor(access = PRIVATE)
@Slf4j
class EnrichProductsProcessor implements AutoCloseable {
    private static final String MISSING_PRODUCT_NAME = "Missing Product Name";

    private final ProductNameProvider productNameProvider;
    private final int bufferSize;
    private final ProductsTemporaryCSVStorage temporaryStorage;

    private List<EnrichedProduct> enrichedProducts = new LinkedList<>();

    void process(ProductParser.Product product) {
        addProduct(product);

        if(isProductBufferFull()) {
            storeProducts();
        }
    }

    @SneakyThrows
    void complete() {
        if (!enrichedProducts.isEmpty()) {
            storeProducts();
        }

        temporaryStorage.waitForComplete();
    }

    private boolean isProductBufferFull() {
        return enrichedProducts.size() >= bufferSize;
    }

    private void addProduct(ProductParser.Product product) {
        String productName = productNameProvider.getProductName(product.productId())
                .orElseGet(() -> {
                    log.warn("Missing mapping for product id: {}", product.productId());
                    return MISSING_PRODUCT_NAME;
                });
        enrichedProducts.add(new EnrichedProduct(productName, product.currency(), product.price(), product.date()));
    }

    private void storeProducts() {
        final List<EnrichedProduct> toStore = enrichedProducts;
        this.temporaryStorage.save(toStore);

        enrichedProducts = new LinkedList<>();
    }

    Path getProcessedProducts() {
        return temporaryStorage.getPath();
    }

    @Override
    public void close() throws Exception {
        temporaryStorage.deleteFile();
    }

    static class EnrichProductsProcessorBuilder {
        private EnrichProductsProcessorBuilder products(List<EnrichedProduct> enrichedProducts) {
            return this;
        }
        public EnrichProductsProcessor build() {
            return new EnrichProductsProcessor(this.productNameProvider, this.bufferSize, temporaryStorage);
        }
    }

    record EnrichedProduct(String productName, String currency, String price, String date){}
}
