package com.github.zkkv.services;

import com.github.zkkv.strategies.ScriptingStrategy;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


public class ScriptRunner {

    private final ScriptEngine engine;

    public ScriptRunner(ScriptingStrategy scriptingStrategy) {
        engine = new ScriptEngineManager().getEngineByExtension(scriptingStrategy.extension());
    }

    public Object executeScript(final String script) throws ScriptException {
        return engine.eval(script);
    }

}
