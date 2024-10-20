package com.johnysoft.highperformanceenricher;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HighPerformanceEnricherApplicationTests {


    private static final Map<String, List<String>> EXPECTED_PRODUCTS = Map.of(
            "product_CRLF.csv", List.of("20160101,Cola,EUR,10.0", "20160101,Fish,EUR,20.1"),
            "product_LF.csv", List.of("20160101,Cola,EUR,10.0", "20160101,Fish,EUR,20.1"),
            "product_extra_columns_CRLF.csv", List.of(
                    "20160101,Cola,EUR,10.0", "20160101,Fish,EUR,20.1",
                    "20160101,Cola,EUR,20.2", "20160101,Fish,EUR,20.3",
                    "20160101,Cola,EUR,20.4"
            ),
            "product_extra_columns_LF.csv", List.of(
                    "20160101,Cola,EUR,10.0", "20160101,Fish,EUR,20.1",
                    "20160101,Cola,EUR,20.2", "20160101,Fish,EUR,20.3",
                    "20160101,Cola,EUR,20.4"
            )
    );

    @LocalServerPort
    private int port;

    private RequestSpecification spec;


    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation) {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(restDocumentation).operationPreprocessors().withRequestDefaults())
                .build();
    }

    @Test
    @SneakyThrows
    void apiDocumentationTest() {
        given(this.spec)
                .contentType("text/csv")
                .filter(document("enriched_products"))
                .when()
                .body(Files.readString(Paths.get("src/test/resources/product_docs.csv")))
                .post("/api/v1/products/enrich")
                .then().assertThat()
                .statusCode(200);
    }


    @ParameterizedTest(name = "returned rows should match to expected data for file {0}")
    @ValueSource(strings = {"product_CRLF.csv", "product_LF.csv", "product_extra_columns_CRLF.csv", "product_extra_columns_LF.csv"})
    @SneakyThrows
    void checkResponsesFromDifferentCSVFilesAndEndLineEncodings(String fileName) {
        List<String> rowsAsLines = new BufferedReader(new InputStreamReader(
                given().log().all()
                        .contentType("text/csv").when()
                        .body(Files.readString(Paths.get("src","test", "resources", fileName)))
                        .post("/api/v1/products/enrich")
                        .then().assertThat().log().all()
                        .statusCode(200)
                        .extract().body().asInputStream()))
                .lines()
                .skip(1)
                .toList();

        assertThat(rowsAsLines).containsExactlyElementsOf(EXPECTED_PRODUCTS.get(fileName));
    }
}
