package com.johnysoft.highperformanceenricher.product.name.population;

import com.johnysoft.highperformanceenricher.product.name.population.ProductsNamesParser.ProductIdWithName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductsNamesParserTest {

    private final ProductsNamesParser parser = new ProductsNamesParser();

    @ParameterizedTest(name = "should parse correctly file {0}")
    @ValueSource(strings = {"product.csv", "product_CRLF.csv"})
    public void shouldParseProductsNames(String fileName) {
        // given
        Path path = pathTo(fileName);

        // when
        List<ProductIdWithName> parse = parser.parse(path);

        // then
        assertThat(parse).hasSize(1);
        assertThat(parse).containsExactly(new ProductIdWithName(2, "Fish"));
    }

    @ParameterizedTest(name = "file {0} with invalid content should be ignored")
    @ValueSource(strings = {"product_no_id.csv","product_no_id_CRLF.csv", "product_no_name.csv", "product_no_name_CRLF.csv"})
    public void shouldIgnoreRowWithNoId(String fileName) {
        // given
        Path path = pathTo(fileName);

        //when
        List<ProductIdWithName> parse = parser.parse(path);

        // then
        assertThat(parse).hasSize(0);
    }


    private Path pathTo(String fileName) {
        return Paths.get("src", "test", "resources", "population", fileName);
    }

}
