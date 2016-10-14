package io.heynow.mapoperator.service.impl;

import groovy.lang.Script;
import io.heynow.mapoperator.pojo.ScriptProcessor;
import io.heynow.mapoperator.service.ScriptService;
import io.heynow.stream.manager.client.facade.StreamManagerClient;
import lombok.RequiredArgsConstructor;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor(onConstructor = @___({@Autowired}))
public class GroovyScriptService implements ScriptService<Script> {

    private final StreamManagerClient streamManagerClient;
    private final CompilerConfiguration compilerConfiguration;

    @Value("${groovy.script.cache.requestbeforecheck:1000}")
    private Long expectedSinceLastResetCheck;

    @Value("${groovy.script.cache.maxunusedclasses:50}")
    private Long maximumUnusedClasses;

    @Value("${groovy.script.cache.markasunusedtime:86400000}")
    private Long markAsUnusedTime;

    private AtomicInteger requestsSinceLastResetCheck = new AtomicInteger();

    private ScriptProcessor scriptProcessor;

    @PostConstruct
    public void init() {
        reset();
    }

    @Override
    public Script getScript(long operatorId) {
        if (requestsSinceLastResetCheck.getAndIncrement() >= expectedSinceLastResetCheck && isResetRequired()) {
            requestsSinceLastResetCheck.set(0);
            reset();
        }
        return scriptProcessor.getScript(operatorId);
    }

    private boolean isResetRequired() {
        return scriptProcessor.getUnusedClassesFor(markAsUnusedTime) >= maximumUnusedClasses;
    }


    private void reset() {
        scriptProcessor = new ScriptProcessor(streamManagerClient, compilerConfiguration);
    }
}
