package com.github.zkkv;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScriptRunner {

    private final ScriptEngine engine;

    private final Path filepath;

    // TODO: think of the best way to handle this
    private final SharedConstants constants;

    public ScriptRunner(final SharedConstants constants) {
        this.constants = constants;
        this.engine = new ScriptEngineManager().getEngineByExtension("kts");
        this.filepath = Paths.get(constants.relativeScriptPath() + constants.scriptName()).toAbsolutePath();
    }

    public void executeScript() {
        // TODO: maybe add a filepath parameter (perhaps to constructor)
        // TODO: see how to change the output destination of the script (i.e. GUI instead of console)

        // final String script = "println(\"Hello, world!!!\")";
        String script;

        try {
            script = Files.readString(filepath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Something went wrong when trying to read from .kts file.");
            return;
        }

        try {
            engine.eval(script);
        } catch (ScriptException e) {
            e.printStackTrace();
            System.err.println("Something went wrong when executing the script.");
            return;
        }

    }

}
