package io.heynow.mapoperator;

import com.google.common.collect.ImmutableMap;
import groovy.transform.TimedInterrupt;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
public class GroovyConfiguration {

    @Value("${groovy.script.timeout:500}")
    private Long timeoutValue;

    @Bean
    CompilerConfiguration configuration(CompilationCustomizer... customizers) {
        return new CompilerConfiguration().addCompilationCustomizers(customizers);
    }

    @Bean
    ASTTransformationCustomizer timedInterrupt() {
        return new ASTTransformationCustomizer(ImmutableMap.of("value", timeoutValue, "unit", MILLISECONDS), TimedInterrupt.class);
    }


}
