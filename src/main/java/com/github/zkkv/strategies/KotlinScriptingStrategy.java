package com.github.zkkv.strategies;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KotlinScriptingStrategy implements ScriptingStrategy {

    @Override
    public String languageName() {
        return "Kotlin";
    }

    @Override
    public String extension() {
        return "kts";
    }

    @Override
    public String exampleSnippet() {
        return """
                fun foo(): Int {
                	var x = 42 * 3
                	return x
                }
                foo()
                """;
    }

    @Override
    public int[] lineAndColumn(String errorLine) {
        // Extract line number and cursor position of the error
        Matcher matcher = Pattern.compile(":(\\d+):(\\d+)").matcher(errorLine);
        if (!matcher.find()) {
            return null;
        }

        int lineNumber = Integer.parseInt(matcher.group(1));
        int columnNumber = Integer.parseInt(matcher.group(2));
        return new int[] {lineNumber, columnNumber};
    }
}
