package com.johnysoft.highperformanceenricher.parsers;

import com.univocity.parsers.common.processor.AbstractRowProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CsvParserFactory {
    private final CsvParserSettings settings;

    public static CsvParserFactory defaultSettings() {
        CsvParserSettings settings = new CsvParserSettings();
        settings.setSkipEmptyLines(true);
        settings.setHeaderExtractionEnabled(true);

        return new CsvParserFactory(settings);
    }

    public CsvParserFactory processor(AbstractRowProcessor abstractRowProcessor) {
        settings.setProcessor(abstractRowProcessor);
        return this;
    }

    public CsvParser buildParser() {
        return new CsvParser(settings);
    }
}
