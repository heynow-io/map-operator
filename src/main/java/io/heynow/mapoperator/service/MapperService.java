package io.heynow.mapoperator.service;


import java.util.Map;

public interface MapperService {
    Map<String, Object> map(long operatorId, Map<String, Object> input);
}
