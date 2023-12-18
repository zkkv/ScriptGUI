package com.github.zkkv.services;

import com.github.zkkv.strategies.ScriptingStrategy;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Executes scripts with the scripting strategy provided in the constructor.
 */
public class ScriptRunner {

    private final ScriptEngine engine;

    /**
     * Instantiates {@code ScriptRunner} with the given {@code scriptingStrategy}.
     * All calls to {@link #executeScript(String)} will use the given scripting language.
     *
     * @param scriptingStrategy the scripting strategy to use for execution of the scripts.
     */
    public ScriptRunner(final ScriptingStrategy scriptingStrategy) {
        engine = new ScriptEngineManager().getEngineByExtension(scriptingStrategy.extension());
    }

    /**
     * Executes the script passed as a String.
     *
     * @param script script to execute.
     * @return result of the execution.
     * @throws ScriptException if there was an error with the script.
     */
    public Object executeScript(final String script) throws ScriptException {
        return engine.eval(script);
    }

}
