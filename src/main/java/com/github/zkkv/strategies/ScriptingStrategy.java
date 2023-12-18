package com.github.zkkv.strategies;

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
}
