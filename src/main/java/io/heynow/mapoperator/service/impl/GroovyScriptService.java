package io.heynow.mapoperator.service.impl;

import io.heynow.mapoperator.service.ScriptService;
import io.heynow.stream.manager.client.facade.StreamManagerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor = @___({@Autowired}))
public class GroovyScriptService implements ScriptService {

    private final StreamManagerClient streamManagerClient;

    @Override
    public String getScript(long operatorId) {
        Map<String, Object> properties = streamManagerClient.getProperties(operatorId);
        return (String) properties.get("script");
    }
}
