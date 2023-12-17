package com.github.zkkv.strategies;

public interface ScriptingStrategy {

    String languageName();

    String extension();

    String exampleSnippet();

    int[] lineAndColumn(final String errorLine);
}
