package com.github.zkkv.services;

import com.github.zkkv.SharedConstants;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScriptRunner {

    private final ScriptEngine engine;

    // private final StringWriter stringWriter;

    public ScriptRunner() {
        engine = new ScriptEngineManager().getEngineByExtension("kts");
        // this.stringWriter = new StringWriter();
        // engine.getContext().setWriter(stringWriter);
    }

    public Object executeScript(SharedConstants constants) throws IOException, ScriptException {
        // TODO: add reading from file to GUIService and here only accept a String

        Path filepath = Paths.get(constants.relativeScriptPath() + constants.scriptName()).toAbsolutePath();
        String script = Files.readString(filepath);
        return engine.eval(script);
    }

}
