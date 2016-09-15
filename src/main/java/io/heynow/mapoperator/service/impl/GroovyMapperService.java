package io.heynow.mapoperator.service.impl;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import io.heynow.mapoperator.service.MapperService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GroovyMapperService implements MapperService {

    public Object map(String script, Map<String, Object> input) {
        Binding binding = new Binding(input);
        GroovyShell shell = new GroovyShell(binding);
        return shell.evaluate(script);
    }
}
