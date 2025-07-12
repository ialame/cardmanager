package com.pcagrade.retriever.cache;

import com.pcagrade.retriever.RetrieverTestConfig;
import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(RetrieverTestConfig.class)
class CacheServiceShould {

    @Autowired
    CacheService cacheService;

    @Test
    void getCached() {
        Supplier<String> supplier = RetrieverTestUtils.spyLambda(Supplier.class, () -> "test");

        var value = cacheService.getCached("test", "test", supplier);
        var value2 = cacheService.getCached("test", "test", supplier);

        Mockito.verify(supplier).get();
        assertThat(value).isEqualTo("test");
        assertThat(value2).isEqualTo("test");
    }

}
