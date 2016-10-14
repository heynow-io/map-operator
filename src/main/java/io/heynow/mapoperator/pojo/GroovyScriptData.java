package io.heynow.mapoperator.pojo;

import groovy.lang.Script;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class GroovyScriptData {

    @Getter
    @Setter
    private volatile long lastUsageTime;

    @Getter
    private Script script;

    public GroovyScriptData(Script script) {
        this.script = script;
        lastUsageTime = System.currentTimeMillis();
    }
}

