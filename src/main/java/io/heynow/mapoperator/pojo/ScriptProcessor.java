package io.heynow.mapoperator.pojo;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import io.heynow.stream.manager.client.facade.StreamManagerClient;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ScriptProcessor {
    private final Map<Long, GroovyScriptData> scripts = new HashMap<>();
    private final StreamManagerClient streamManagerClient;
    private final GroovyShell groovyShell;

    public Script getScript(long key) {
        if (!scripts.containsKey(key)) {
            return generateScript(key);
        } else {
            GroovyScriptData groovyScriptData = scripts.get(key);
            groovyScriptData.setLastUsageTime(System.currentTimeMillis());
            return groovyScriptData.getScript();
        }
    }

    private Script generateScript(long key) {
        synchronized (scripts) {
            if (scripts.containsKey(key)) {
                String scriptText = (String) streamManagerClient.getProperties(key).get("script");
                Script script = groovyShell.parse(scriptText);
                scripts.put(key, new GroovyScriptData(script));

            }
        }
        return scripts.get(key).getScript();
    }

    public long getUnusedClassesFor(long markAsUnusedTime) {

    }
}