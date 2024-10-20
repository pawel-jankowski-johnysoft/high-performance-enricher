package com.johnysoft.highperformanceenricher.product.name.population;

import com.johnysoft.highperformanceenricher.parsers.CsvParserFactory;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.AbstractRowProcessor;
import com.univocity.parsers.csv.CsvParser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static java.nio.file.Files.newBufferedReader;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
class ProductsNamesParser {

    List<ProductIdWithName> parse(Path path) {
        if (Files.notExists(path)) {
         return Collections.emptyList();
        }

        return readProductsWithNames(path);
    }

    @SneakyThrows
    private List<ProductIdWithName> readProductsWithNames(Path path) {
        List<ProductIdWithName> productIdWithNames = new LinkedList<>();

        CsvParser parser = CsvParserFactory.defaultSettings()
                .processor(new ProductIdWithNameProcessor(productIdWithNames::add))
                .buildParser();

        parser.parse(newBufferedReader(path));

        return productIdWithNames;
    }


    record ProductIdWithName(Integer productId,String productName){ }

    @RequiredArgsConstructor(access = PRIVATE)
    private static class ProductIdWithNameProcessor extends AbstractRowProcessor {
        private final Consumer<ProductIdWithName> action;
        private int productIdIndex;
        private int productNameIndex;
        @Override
        public void processStarted(ParsingContext context) {
            productIdIndex = context.indexOf("productId");
            productNameIndex = context.indexOf("productName");
        }

        @Override
        public void rowProcessed(String[] row, ParsingContext context) {
            try {
                action.accept(new ProductIdWithName(Integer.valueOf(row[productIdIndex]), requireNonNull(row[productNameIndex])));
            } catch (Exception e) {
                log.warn("Could not parse product id {}", row[productIdIndex], e);
            }
        }
    }
}
