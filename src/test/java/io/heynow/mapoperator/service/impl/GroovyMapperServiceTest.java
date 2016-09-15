package io.heynow.mapoperator.service.impl;


import com.google.common.collect.ImmutableMap;
import io.heynow.mapoperator.service.MapperService;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GroovyMapperServiceTest {

    private static final String SAMPLE_KEY = "key";
    private static final String SAMPLE_VALUE = "value";


    private MapperService mapperService = new GroovyMapperService();


    @Test
    public void shouldAutomapWithoutChange() {
        Object result = mapperService.map("key", ImmutableMap.of(SAMPLE_KEY, SAMPLE_VALUE));
        assertThat(result).isEqualTo(SAMPLE_VALUE);
    }

    @Test
    public void shouldRunGroovySyntax() {
        Object result = mapperService.map("key * 2", ImmutableMap.of(SAMPLE_KEY, SAMPLE_VALUE));
        assertThat(result).isEqualTo(SAMPLE_VALUE + SAMPLE_VALUE);
    }
}
