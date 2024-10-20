package com.johnysoft.highperformanceenricher.product.name.population;

import com.johnysoft.highperformanceenricher.product.name.population.ProductsNamesParser.ProductIdWithName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductNamesStorageTest {

    private final ProductIdWithName anyProductIdWithName = new ProductIdWithName(1, "productName");
    @Mock
    private ProductsNamesParser productsNamesParser;

    @InjectMocks
    private ProductNamesStorage productNamesStorage;

    @Test
    public void shouldUsePopulatedProductName() {
       // given
        var anyPath = Paths.get("any");
        var anyProductIdWithNames = anyProductIdWithNameList();
        when(productsNamesParser.parse(ArgumentMatchers.eq(anyPath)))
                .thenReturn(anyProductIdWithNames);
        // and
        productNamesStorage.populate(anyPath);

        // expect
        assertThat(productNamesStorage.getProductName(anyProductIdWithName.productId()))
                .hasValue(anyProductIdWithName.productName());
    }

    private List<ProductIdWithName> anyProductIdWithNameList() {
        return List.of(anyProductIdWithName);
    }
}
