package com.github.zkkv.services;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


public class ScriptRunner {

    private final ScriptEngine engine;

    public ScriptRunner() {
        engine = new ScriptEngineManager().getEngineByExtension("kts");
    }

    public Object executeScript(final String script) throws ScriptException {
        return engine.eval(script);
    }

}
