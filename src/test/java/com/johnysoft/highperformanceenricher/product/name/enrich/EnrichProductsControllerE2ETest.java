package com.johnysoft.highperformanceenricher.product.name.enrich;

import io.restassured.RestAssured;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static io.restassured.RestAssured.given;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "products.buffer.size=1")
class EnrichProductsControllerE2ETest {


    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @RepeatedTest(1000)
    @SneakyThrows
    void eachEnrichedRowShouldBeInNewLine() {
        var responseBody = given().contentType("text/csv")
                .body(Files.newInputStream(Path.of("src", "test", "resources", "product_CRLF.csv")))
                .when()
                .post("/api/v1/products/enrich")
                .then().statusCode(200)
                .and().extract().body().asInputStream();
        var lines = enrichedProducts(responseBody);

        assertThat(lines)
                .containsExactlyElementsOf(of("20160101,Cola,EUR,10.0", "20160101,Fish,EUR,20.1"));
    }

    private List<String> enrichedProducts(InputStream responseBody) {
        return new BufferedReader(new InputStreamReader(responseBody))
                .lines().skip(1).toList();
    }
}
