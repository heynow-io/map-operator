package io.heynow.mapperoperator.service.impl;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import io.heynow.mapperoperator.service.MapperService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GroovyMapperService implements MapperService {

    public Object map(String script, Map<String, Object> input) {
        Binding binding = new Binding();
        binding.setVariable("input", input);
        GroovyShell shell = new GroovyShell(binding);
        Object evaluate = shell.evaluate(script);
        return evaluate;
    }
}
