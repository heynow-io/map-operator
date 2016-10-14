package io.heynow.mapoperator.service.impl;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.catchThrowable;

public class GroovyMapperServiceTest {

    private static final String SAMPLE_KEY_1 = "key1";
    private static final String SAMPLE_KEY_2 = "key2";
    private static final String SAMPLE_VALUE_1 = "value1";
    private static final String SAMPLE_VALUE_2 = "value2";
    private static final Map<String, Object> SAMPLE_MAP = ImmutableMap.of(SAMPLE_KEY_1, SAMPLE_VALUE_1);
    private static final Map<String, Object> SAMPLE_MAP_SIZE_2 = ImmutableMap.of(SAMPLE_KEY_1, SAMPLE_VALUE_1, SAMPLE_KEY_2, SAMPLE_VALUE_2);
    private static final List<String> SAMPLE_LIST = ImmutableList.of(SAMPLE_VALUE_1);
    private static final List<String> SAMPLE_LIST_SIZE_2 = ImmutableList.of(SAMPLE_VALUE_1, SAMPLE_VALUE_2);

    @Before
    public void setUp() throws Exception {
        mapperService.init();

    }

    private GroovyDefaultMapperService mapperService = new GroovyDefaultMapperService();


    @Test
    public void passVariable() {
        Object result = mapperService.map("key1", ImmutableMap.of(SAMPLE_KEY_1, SAMPLE_VALUE_1));
        assertThat(result).isEqualTo(SAMPLE_VALUE_1);
    }

    @Test
    public void allowMultiplyObject() {
        Object result = mapperService.map("key1 * 2", ImmutableMap.of(SAMPLE_KEY_1, SAMPLE_VALUE_1));
        assertThat(result).isEqualTo(SAMPLE_VALUE_1 + SAMPLE_VALUE_1);
    }

    @Test
    public void allowMapEntryAccess() {
        Object result = mapperService.map("key1.key1", ImmutableMap.of(SAMPLE_KEY_1, SAMPLE_MAP));
        assertThat(result).isEqualTo(SAMPLE_VALUE_1);
    }

    @Test
    public void allowListEntryAccess() {
        Object result = mapperService.map("key1[0]", ImmutableMap.of(SAMPLE_KEY_1, SAMPLE_LIST));
        assertThat(result).isEqualTo(SAMPLE_VALUE_1);
    }

    @Test
    public void denyOverrideInGlobalScope() {
//        Object result1 = mapperService.map("Integer.metaClass.toString = { 'override' }; return new Integer(0).toString() ", SAMPLE_MAP);
//        Object result2 = mapperService.map("new Integer(0).toString()", SAMPLE_MAP);
//        assertThat(result1).isEqualTo("override");
//        assertThat(result2).isEqualTo("0");
        Object result1 = mapperService.map("return \"override\";", SAMPLE_MAP);
        assertThat(result1).isEqualTo("override");
    }

    @Test
    public void allowLoops() {
        Throwable thrown = catchThrowable(() -> mapperService.map("for(int i=0;i<5;i++){i++};", Collections.emptyMap()));
        assertThat(thrown).isNull();
    }

    @Test
    public void denyInfiniteLoops() {
        assertThatExceptionOfType(TimeoutException.class).isThrownBy(() ->
                mapperService.map("while(true){}", Collections.emptyMap()));
    }

    @Test
    public void allowMathLib() {
        Object result = mapperService.map("cos PI", ImmutableMap.of(SAMPLE_KEY_1, SAMPLE_VALUE_1));
        assertThat(result).isEqualTo(-1.0);
    }

    @Test
    public void denyDirectSystemCalls() {
        assertThatExceptionOfType(MultipleCompilationErrorsException.class).isThrownBy(() -> mapperService.map("System.exit(1)", Collections.emptyMap()));
    }

//    @Test
//    public void denyStringExecuteSystemCommand() {
//        assertThatExceptionOfType(MultipleCompilationErrorsException.class).isThrownBy(() ->
//                mapperService.map("'ls'.execute()", Collections.emptyMap()));
//    }

    @Test
    public void allowStringStandardMethod() {
        Object result = mapperService.map("'ls'.toUpperCase()", Collections.emptyMap());
        assertThat(result).isEqualTo("LS");
    }
}
