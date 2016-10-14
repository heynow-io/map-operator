package io.heynow.mapoperator;

import com.google.common.collect.ImmutableMap;
import groovy.transform.TimedInterrupt;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
public class GroovyConfiguration {

    @Value("${groovy.script.timeout:500}")
    private Long timeoutValue;

    @Bean
    CompilerConfiguration configuration() {
        return new CompilerConfiguration().addCompilationCustomizers(getTimeoutConfiguration());
    }

    private ASTTransformationCustomizer getTimeoutConfiguration() {
        Map<String, Object> params = ImmutableMap.<String, Object>builder().put("value", timeoutValue).put("unit",
                GeneralUtils.propX(GeneralUtils.classX(TimeUnit.class), MILLISECONDS.toString())).build();
        return new ASTTransformationCustomizer(params, TimedInterrupt.class);
    }


}
