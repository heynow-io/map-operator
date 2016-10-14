package io.heynow.mapoperator.service.impl;

import groovy.lang.Binding;
import groovy.lang.Script;
import io.heynow.mapoperator.service.MapperService;
import io.heynow.mapoperator.service.ScriptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GroovyDefaultMapperService implements MapperService {

    private final ScriptService<Script> scriptService;


    @Override
    public Map<String, Object> map(long operatorId, Map<String, Object> input) {
        Script script = scriptService.getScript(operatorId);
        script.setBinding(new Binding(input));
        return getMapFromObject(script.run());
    }

    private Map<String, Object> getMapFromObject(Object input) {
        Map<String, Object> result;
        if (input instanceof Map) {
            result = ((Map<Object, Object>) input).entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue));
        } else {
            result = new HashMap<>();
            result.put("result", input);
        }
        return result;
    }
}
