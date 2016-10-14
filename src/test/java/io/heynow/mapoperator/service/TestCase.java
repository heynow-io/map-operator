package io.heynow.mapoperator.service;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class TestCase {
    private final long operatorId;
    private final Map<String, Object> input;
    private final Map<String, Object> script;
    private final Map<String, Object> expectedResult;
}
