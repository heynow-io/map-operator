package io.heynow.mapoperator.service.impl;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import io.heynow.mapoperator.pojo.GroovyScriptData;
import io.heynow.mapoperator.service.ScriptService;
import io.heynow.stream.manager.client.facade.StreamManagerClient;
import lombok.RequiredArgsConstructor;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @___({@Autowired}))
public class GroovyScriptService implements ScriptService<Script> {

    private final StreamManagerClient streamManagerClient;
    private final CompilerConfiguration compilerConfiguration;

    @Override
    public Script getScript(long operatorId) {
        return getScriptData(operatorId).getScript();
    }

    @Cacheable(cacheNames = "scriptsData", key = "#operatorId")
    public GroovyScriptData getScriptData(long operatorId) {
        String scriptText = (String) streamManagerClient.getProperties(operatorId).get("script");
        GroovyShell groovyShell = new GroovyShell(compilerConfiguration);
        Script script = groovyShell.parse(scriptText);
        return new GroovyScriptData(groovyShell, script);
    }

}
