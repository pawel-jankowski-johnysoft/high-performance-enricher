package com.johnysoft.highperformanceenricher.product.name.enrich;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.String.join;
import static java.lang.System.lineSeparator;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.writeString;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.joining;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
class ProductsTemporaryCSVStorage {

    private static final String[] HEADERS = {"date", "productName", "currency", "price"};
    private final Path csvFile;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    void save(final List<EnrichProductsProcessor.EnrichedProduct> enrichedProducts) {
        executor.submit(() -> toFile(enrichedProducts));
    }
    @SneakyThrows
    private void toFile(List<EnrichProductsProcessor.EnrichedProduct> enrichedProducts) {
        writeString(csvFile, endedByNewLine(enrichedProducts.stream().map(this::parse).collect(joining(lineSeparator()))), APPEND);
    }

    private String parse(EnrichProductsProcessor.EnrichedProduct enrichedProduct) {
        return join(",", enrichedProduct.date(), enrichedProduct.productName(), enrichedProduct.currency(), enrichedProduct.price());
    }

    @SneakyThrows
    static ProductsTemporaryCSVStorage create() {
        var productsTemporaryStorage = new ProductsTemporaryCSVStorage(createTempFile(randomUUID().toString(), ".csv"));
        productsTemporaryStorage.init();

        return productsTemporaryStorage;
    }

    @SneakyThrows
    private void init() {
        writeString(csvFile, endedByNewLine(join(",", HEADERS)), APPEND);
    }

    private String endedByNewLine(String content) {
        return content + lineSeparator();
    }

    @SneakyThrows
    public void deleteFile() {
        Files.delete(csvFile);
    }

    public Path getPath() {
        return csvFile;
    }

    @SneakyThrows
    public void waitForComplete() {
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
}
