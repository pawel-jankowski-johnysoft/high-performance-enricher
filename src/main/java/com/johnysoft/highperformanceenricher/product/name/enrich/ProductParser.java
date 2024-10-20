package com.johnysoft.highperformanceenricher.product.name.enrich;

import com.johnysoft.highperformanceenricher.parsers.CsvParserFactory;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.AbstractRowProcessor;
import com.univocity.parsers.csv.CsvParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
class ProductParser {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    void parse(InputStream inputStream, Consumer<Product> processProduct) {
        CsvParser csvParser = CsvParserFactory.defaultSettings()
                .processor(new ProductProcessor(processProduct))
                .buildParser();

        csvParser.parse(inputStream);
    }



    record Product(String date, Integer productId, String currency, String price) {}

    @RequiredArgsConstructor(access = PRIVATE)
    private static class ProductProcessor extends AbstractRowProcessor {
        private final Consumer<Product> action;

        private int dateIndex;
        private int productIdIndex;
        private int currencyIndex;
        private int priceIndex;

        @Override
        public void processStarted(ParsingContext context) {
            dateIndex = context.indexOf("date");
            productIdIndex = context.indexOf("productId");
            currencyIndex = context.indexOf("currency");
            priceIndex = context.indexOf("price");
        }

        @Override
        public void rowProcessed(String[] row, ParsingContext context) {
            try {
                action.accept(new Product(validatedDate(row[dateIndex]), Integer.valueOf(row[productIdIndex]), row[currencyIndex], row[priceIndex]));
            } catch (Exception e) {
                log.error("Error parsing product", e);
            }
        }

        private String validatedDate(String date) {
                LocalDate.parse(date, FORMATTER);
                return date;
        }
    }
}
