package com.github.zkkv.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GUIService {

    public void saveCodeToFile(final Path filepath, final String code) throws IOException {
        Files.writeString(filepath, code);
    }

    public String readCodeFromFile(final Path filepath) throws IOException {
        return Files.readString(filepath);
    }
}
