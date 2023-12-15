package com.github.zkkv;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptRunner {

    private final ScriptEngine engine;

    public ScriptRunner() {
        this.engine = new ScriptEngineManager().getEngineByExtension("kts");
    }

    public void executeScript() {
        final String script = "println(\"Hello, world!!!\")";

        try {
            engine.eval(script);
        } catch (ScriptException e) {
            e.printStackTrace();
            System.out.println("Something went wrong when executing the script.");
        }

    }

}
