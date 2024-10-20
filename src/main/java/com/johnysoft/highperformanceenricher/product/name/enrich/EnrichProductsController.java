package com.johnysoft.highperformanceenricher.product.name.enrich;


import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
class EnrichProductsController {
    private final ProductEnricher productEnricher;

    @PostMapping(value = "/enrich")
    public ResponseEntity<?> enrichProducts(InputStream csv) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(productEnricher.enrich(csv)));

     }

}


