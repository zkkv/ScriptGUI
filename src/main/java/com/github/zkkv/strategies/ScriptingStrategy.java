package com.github.zkkv.strategies;

import java.util.regex.Pattern;

/**
 * Defines an interface for a set of scripting languages.
 */
public interface ScriptingStrategy {

    /**
     * Returns the scripting language name in human-readable format.
     *
     * @return the scripting language name in human-readable format.
     */
    String languageName();

    /**
     * Returns the language extension name for proper execution of the script by scripting engine.
     *
     * @return the language extension name.
     */
    String extension();

    /**
     * Example snippet which can be used to test the program or set fill up the code area.
     *
     * @return example snippet with working code.
     */
    String exampleSnippet();

    /**
     * Returns an {@code int} array of size 2 that contains the line and column numbers
     * where the error in {@code errorLine} occurred.
     *
     * @param errorLine error string which contains the line and column numbers of the error.
     * @return array of size 2 that contains the line number at index 0 and
     * column number at index 1, if it was possible to find the two in the {@code errorLine}.
     * Otherwise, {@code null}.
     */
    int[] lineAndColumn(String errorLine);

    /**
     * Returns {@code true} if the {@code errorLine} is an error and {@code false} otherwise.
     *
     * @param errorLine string with a message
     * @return {@code true} if the message is an error and {@code false} otherwise.
     */
    boolean isError(String errorLine);

    /**
     * Returns {@code true} if the {@code errorLine} is a warning and {@code false} otherwise.
     *
     * @param errorLine string with a message
     * @return {@code true} if the message is a warning and {@code false} otherwise.
     */
    boolean isWarning(String errorLine);

    /**
     * Returns a pattern matching language keywords.
     *
     * @return a pattern matching language keywords.
     */
    Pattern keywordPattern();
}
