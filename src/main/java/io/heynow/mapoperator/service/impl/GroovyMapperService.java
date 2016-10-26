package io.heynow.mapoperator.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableMap;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GroovyMapperService implements MapperService {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private final ScriptService<Script> scriptService;


    @Override
    public Map<String, Object> map(long operatorId, Map<String, Object> input) {
        Script script = scriptService.getScript(operatorId);
        Map<String, Object> bindingInput = new HashMap<>();
        bindingInput.put("input", input);
        script.setBinding(new Binding(bindingInput));
        return getMapFromObject(script.run());
    }

    private Map<String, Object> getMapFromObject(Object input) {
        return returnIfMap(input)
                .orElseGet(() -> convertToStringMapIfValueOrList(input)
                        .orElseGet(() -> convertToStringMap(input)));
    }

    private Map<String, Object> convertToStringMap(Map<Object, Object> input) {
        return input.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue));
    }

    private Optional<Map<String, Object>> returnIfMap(Object input) {
        return Optional.ofNullable(input)
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(this::convertToStringMap);
    }

    private Optional<Map<String, Object>> convertToStringMapIfValueOrList(Object input) {
        return Optional.ofNullable(mapper.convertValue(input, JsonNode.class))
                .filter(node -> node.isValueNode() || node.isArray())
                .map(node -> ImmutableMap.of("result", input));
    }

    private Map convertToStringMap(Object input) {
        return mapper.convertValue(input, Map.class);
    }
}
