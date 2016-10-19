package io.heynow.mapoperator.service

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import io.heynow.mapoperator.Application
import io.heynow.mapoperator.service.impl.GroovyScriptService
import io.heynow.stream.manager.client.facade.StreamManagerClient
import org.codehaus.groovy.control.CompilerConfiguration
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = [Application.class, Confing.class])
@WebIntegrationTest
class GroovyMapperServiceTest {

    final String DEFAULT_RESULT_KEY = "result";
    final String SCRIPT_KEY = "script";

    final Map<String, Object> SIMPLE_MAP = ImmutableMap.of("key1", "1'");
    final Map<String, Object> COMPLEX_MAP = ImmutableMap.of("key1", ImmutableMap.of(new Object(), new Object()));


    final TestCase PREDEFINED_RETURN_SCRIPT = new TestCase(1L, ImmutableMap.of(), ImmutableMap.of(SCRIPT_KEY, "5"), ImmutableMap.of(DEFAULT_RESULT_KEY, 5));

    final TestCase PREDEFINED_RETURN_WITH_INPUT_SCRIPT = new TestCase(2L, ImmutableMap.of("key", "1"), ImmutableMap.of(SCRIPT_KEY, "5"), ImmutableMap.of(DEFAULT_RESULT_KEY, 5));

    final TestCase PARAMETER_USAGE_SCRIPT = new TestCase(3L, ImmutableMap.of("key", "1"), ImmutableMap.of(SCRIPT_KEY, "input.key"), ImmutableMap.of(DEFAULT_RESULT_KEY, "1"));
    final TestCase MULTI_PARAMETER_USAGE_SCRIPT =
            new TestCase(4L, ImmutableMap.of("key1", 1, "key2", 2), ImmutableMap.of(SCRIPT_KEY, "input.key1 + input.key2"), ImmutableMap.of(DEFAULT_RESULT_KEY, 3));
    final TestCase LOOP_SCRIPT =
            new TestCase(5L, ImmutableMap.of("key", ImmutableList.of(1, 2, 3)), ImmutableMap.of(SCRIPT_KEY, "int acc=0;for(i in input.key){acc+=i};return acc"), ImmutableMap.of(DEFAULT_RESULT_KEY, 6));

    final TestCase INFINITE_LOOP_SCRIPT = new TestCase(6L, ImmutableMap.of(), ImmutableMap.of(SCRIPT_KEY, "int acc=0;while(true)acc++;return acc"), null);
    final TestCase PASS_INPUT_SCRIPT = new TestCase(7L, SIMPLE_MAP, ImmutableMap.of(SCRIPT_KEY, "input"), SIMPLE_MAP);
    final TestCase PASS_COMPLEX_INPUT_SCRIPT = new TestCase(8L, COMPLEX_MAP, ImmutableMap.of(SCRIPT_KEY, "input"), COMPLEX_MAP);

    @Autowired
    private MapperService mapperService;

    @Test
    public void returnPredefinedValue() {
        Map<String, Object> result = mapperService.map(PREDEFINED_RETURN_SCRIPT.getOperatorId(), PREDEFINED_RETURN_SCRIPT.getInput());
        assertThat(result).isEqualTo(PREDEFINED_RETURN_SCRIPT.getExpectedResult());
    }

    @Test
    public void returnPredefinedWIthInputValue() {
        Map<String, Object> result = mapperService.map(PREDEFINED_RETURN_WITH_INPUT_SCRIPT.getOperatorId(), PREDEFINED_RETURN_WITH_INPUT_SCRIPT.getInput());
        assertThat(result).isEqualTo(PREDEFINED_RETURN_WITH_INPUT_SCRIPT.getExpectedResult());
    }

    @Test
    public void parameterUsage() {
        Map<String, Object> result = mapperService.map(PARAMETER_USAGE_SCRIPT.getOperatorId(), PARAMETER_USAGE_SCRIPT.getInput());
        assertThat(result).isEqualTo(PARAMETER_USAGE_SCRIPT.getExpectedResult());
    }

    @Test
    public void multiParameterUsage() {
        Map<String, Object> result = mapperService.map(MULTI_PARAMETER_USAGE_SCRIPT.getOperatorId(), MULTI_PARAMETER_USAGE_SCRIPT.getInput());
        assertThat(result).isEqualTo(MULTI_PARAMETER_USAGE_SCRIPT.getExpectedResult());
    }

    @Test
    public void loopUsage() {
        Map<String, Object> result = mapperService.map(LOOP_SCRIPT.getOperatorId(), LOOP_SCRIPT.getInput());
        assertThat(result).isEqualTo(LOOP_SCRIPT.getExpectedResult());
    }

//    @Test
//    public void infiniteLoopUsage() {
//        Throwable throwable = catchThrowable(_ -> { mapperService.map(INFINITE_LOOP_SCRIPT.getOperatorId(), INFINITE_LOOP_SCRIPT.getInput()) });
//        assertThat(throwable).isInstanceOf(TimeoutException.class);
//    }

    @Test
    public void passSimpleInput() {
        Map<String, Object> result = mapperService.map(PASS_INPUT_SCRIPT.getOperatorId(), PASS_INPUT_SCRIPT.getInput());
        assertThat(result).isEqualTo(PASS_INPUT_SCRIPT.getExpectedResult());
    }

    @Test
    public void passComplexInput() {
        Map<String, Object> result = mapperService.map(PASS_COMPLEX_INPUT_SCRIPT.getOperatorId(), PASS_COMPLEX_INPUT_SCRIPT.getInput());
        assertThat(result).isEqualTo(PASS_COMPLEX_INPUT_SCRIPT.getExpectedResult());
    }

    @Configuration
    class Confing {
        @Bean
        GroovyScriptService groovyScriptService(CompilerConfiguration compilerConfiguration) {
            StreamManagerClient mock = mock(StreamManagerClient.class);
            when(mock.getProperties(PREDEFINED_RETURN_SCRIPT.getOperatorId())).thenReturn(PREDEFINED_RETURN_SCRIPT.getScript());
            when(mock.getProperties(PREDEFINED_RETURN_WITH_INPUT_SCRIPT.getOperatorId())).thenReturn(PREDEFINED_RETURN_WITH_INPUT_SCRIPT.getScript());
            when(mock.getProperties(PARAMETER_USAGE_SCRIPT.getOperatorId())).thenReturn(PARAMETER_USAGE_SCRIPT.getScript());
            when(mock.getProperties(MULTI_PARAMETER_USAGE_SCRIPT.getOperatorId())).thenReturn(MULTI_PARAMETER_USAGE_SCRIPT.getScript());
            when(mock.getProperties(LOOP_SCRIPT.getOperatorId())).thenReturn(LOOP_SCRIPT.getScript());
            when(mock.getProperties(INFINITE_LOOP_SCRIPT.getOperatorId())).thenReturn(INFINITE_LOOP_SCRIPT.getScript());
            when(mock.getProperties(PASS_INPUT_SCRIPT.getOperatorId())).thenReturn(PASS_INPUT_SCRIPT.getScript());
            when(mock.getProperties(PASS_COMPLEX_INPUT_SCRIPT.getOperatorId())).thenReturn(PASS_COMPLEX_INPUT_SCRIPT.getScript());
            return new GroovyScriptService(mock, compilerConfiguration);
        }
    }

}




