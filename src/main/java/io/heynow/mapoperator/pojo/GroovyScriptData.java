package io.heynow.mapoperator.pojo;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroovyScriptData {
    private GroovyShell groovyShell;
    private Script script;
}

