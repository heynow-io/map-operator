package io.heynow.mapperoperator.service;


import java.util.Map;

public interface MapperService {
    Object map(String script, Map<String, Object> input);
}
