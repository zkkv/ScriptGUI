package com.github.zkkv;

public class Main {
    public static void main(String[] args) {
        SharedConstants constants = new SharedConstants("src/main/java/com/github/zkkv/", "script.kts");
        ScriptRunner scriptRunner = new ScriptRunner(constants);
        scriptRunner.executeScript();
    }
}