package com.johnysoft.highperformanceenricher.product.name.enrich;

import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductEnricherTest {

    @Autowired
    private ProductEnricher productEnricher;

    @ParameterizedTest(name = "should enrich product by product name when product name exists ({0})")
    @ValueSource(strings = {"enrichable_product.csv", "enrichable_product_CRLF.csv"})
    @SneakyThrows
    void shouldEnrichProductByProductNameWhenProductNameExists(String fileName) {
        // given
        var anyCsvWithEnrichableProductName =  pathTo(fileName);

        // when
        InputStream enriched = productEnricher.enrich(Files.newInputStream(anyCsvWithEnrichableProductName));

        //then
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(enriched));
        List<String> csvRows = streamReader.lines().toList();
        assertThat(csvRows.size()).isEqualTo(2);

        String product = csvRows.get(1);
        String[] fields = product.split(",");

        assertThat(fields.length).isEqualTo(4);

        assertThat(fields[0]).isEqualTo("20160101");
        assertThat(fields[1]).isEqualTo("Cola");
        assertThat(fields[2]).isEqualTo("EUR");
        assertThat(fields[3]).isEqualTo("10.0");
    }

    @ParameterizedTest(name = "should enrich product using missing product name when product does not exist({0})")
    @ValueSource(strings = {"unenrichable_product.csv", "unenrichable_product_CRLF.csv"})
    @SneakyThrows
    void shouldEnrichProductUsingMissingProductNameWhenProductNameDoesNotExist(String fileName) {
        // given
        var anyCsvWithUnenrichableProductName =  pathTo(fileName);

        // when
        InputStream enriched = productEnricher.enrich(Files.newInputStream(anyCsvWithUnenrichableProductName));

        //then
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(enriched));
        List<String> csvRows = streamReader.lines().toList();
        assertThat(csvRows.size()).isEqualTo(2);

        String product = csvRows.get(1);
        String[] fields = product.split(",");

        assertThat(fields.length).isEqualTo(4);

        assertThat(fields[0]).isEqualTo("20160101");
        assertThat(fields[1]).isEqualTo("Missing Product Name");
        assertThat(fields[2]).isEqualTo("EUR");
        assertThat(fields[3]).isEqualTo("10.0");
    }

    @ParameterizedTest(name = "should ignore invalid products ({0})")
    @SneakyThrows
    @ValueSource(strings = {"invalid_products.csv", "invalid_products_CRLF.csv"})
    void shouldIgnoreInvalidProducts(String fileName) {
        // given
        var anyCsvWithUnenrichableProductName =  pathTo(fileName);

        // when
        InputStream enriched = productEnricher.enrich(Files.newInputStream(anyCsvWithUnenrichableProductName));

        //then
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(enriched));
        List<String> csvRows = streamReader.lines().toList();

        // contains only CSV header
        assertThat(csvRows.size()).isEqualTo(1);
        assertThat(csvRows.get(0)).isEqualTo("date,productName,currency,price");
    }


    private Path pathTo(String fileName) {
        return Paths.get("src", "test", "resources", "product", fileName);
    }
}
