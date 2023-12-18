package com.github.zkkv.strategies;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * See {@link ScriptingStrategy}.
 */
public class KotlinScriptingStrategy implements ScriptingStrategy {

    public static final String[] KEYWORDS = new String[] {
            "as", "break", "class", "continue", "do", "else", "false", "for",
            "fun", "if", "in", "interface", "is", "null", "object", "package",
            "return", "super", "this", "throw", "true", "try", "typealias",
            "typeof", "val", "var", "when", "while"
    };

    /**
     * See {@link ScriptingStrategy#languageName()}.
     */
    @Override
    public String languageName() {
        return "Kotlin";
    }

    /**
     * See {@link ScriptingStrategy#extension()}.
     */
    @Override
    public String extension() {
        return "kts";
    }

    /**
     * See {@link ScriptingStrategy#exampleSnippet()}.
     */
    @Override
    public String exampleSnippet() {
        return """
                fun foo(): Int {
                    var x = 42 * 3
                    for (i in 1..10000) {
                        println(i)
                        if (i == 12345) {
                            break
                        }
                        Thread.sleep(100)
                    }
                    return x
                }
                foo()
                """;
    }

    /**
     * See {@link ScriptingStrategy#lineAndColumn(String)}.
     */
    @Override
    public int[] lineAndColumn(final String errorLine) {
        // Extract line number and cursor position of the error
        Matcher matcher = Pattern.compile(":(\\d+):(\\d+)").matcher(errorLine);
        if (!matcher.find()) {
            return null;
        }

        int lineNumber = Integer.parseInt(matcher.group(1));
        int columnNumber = Integer.parseInt(matcher.group(2));
        return new int[] {lineNumber, columnNumber};
    }

    /**
     * See {@link ScriptingStrategy#isError(String)}.
     */
    @Override
    public boolean isError(final String errorLine) {
        return Pattern.compile("(ERROR)").matcher(errorLine).find();
    }

    /**
     * See {@link ScriptingStrategy#isWarning(String)}.
     */
    @Override
    public boolean isWarning(final String errorLine) {
        return Pattern.compile("(WARNING)").matcher(errorLine).find();
    }

    /**
     * See {@link ScriptingStrategy#keywordPattern()}.
     */
    @Override
    public Pattern keywordPattern() {
        return Pattern.compile("\\b(" + String.join("|", KEYWORDS) + ")\\b");
    }
}
