package io.heynow.mapoperator.service

import io.heynow.mapoperator.Application
import io.heynow.mapoperator.service.impl.GroovyMapperService
import io.heynow.mapoperator.service.impl.GroovyScriptService
import io.heynow.stream.manager.client.facade.StreamManagerClient
import org.codehaus.groovy.control.CompilerConfiguration
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.annotation.Configuration
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant
import java.util.concurrent.TimeoutException

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@SpringApplicationConfiguration(classes = [Application.class])
@Configuration
class GroovyMapperServiceTest extends Specification {

    @Autowired
    CompilerConfiguration compilerConfiguration;


    private MapperService mapperService;
    private StreamManagerClient streamManagerClient;

    @Before
    public void init() {
        streamManagerClient = mock(StreamManagerClient);
        mapperService = new GroovyMapperService(new GroovyScriptService(streamManagerClient, compilerConfiguration));
    }

    @Unroll
    def "#description"(String script, Map input, Map expectedOutput, String description) {
        given:
            when(streamManagerClient.getProperties(0)).thenReturn([script: script])
            Map<String, Object> result = mapperService.map(0, input)
        expect:
            result == expectedOutput
        where:
            script                                             | input              | expectedOutput                     | description
            "5"                                                | [:]                | [result: 5]                        | "return predefined value"
            "input"                                            | [key: 1]           | [key: 1]                           | "input to output"
            "input"                                            | [key: [[], []]]    | [key: [[], []]]                    | "complex input to output"
            "5"                                                | [key: 1]           | [result: 5]                        | "return predefined with input value"
            "input.key"                                        | [key: 1]           | [result: 1]                        | "parameter usage"
            "input.key1 + input.key2"                          | [key1: 1, key2: 2] | [result: 3]                        | "multi parameter usage"
            "int acc=0;for(i in input.key){acc+=i};return acc" | [key: [1, 2, 3]]   | [result: 6]                        | "loop"
            "class A{A a;int b} \n new A(a:new A(b:5),b:1)"    | [:]                | [a: [a: null, b: 5], b: 1]         | "custome nasted object output"
            "class A{int a;int b} \n new A(a:1,b:2)"           | [:]                | [a: 1, b: 2]                       | "custome int pair object output"
            "Date.from(java.time.Instant.EPOCH)"               | [:]                | [result: Date.from(Instant.EPOCH)] | "date"
            "[1,2,3]"                                          | [:]                | [result: [1, 2, 3]]                | "list"
    }

    @Test
    def "inifnite loop"() {
        given:
            when(streamManagerClient.getProperties(0)).thenReturn([script: "int acc=0;while(true)acc++;return acc"]);
        when:
            mapperService.map(0, [:])
        then:
            thrown TimeoutException
    }
}