package com.github.zkkv.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Provides utility methods for GUIController to use.
 */
public class GUIService {

    public void saveCodeToFile(final Path filePath, final String code) throws IOException {
        Files.writeString(filePath, code);
    }

    public String readCodeFromFile(final Path filePath) throws IOException {
        return Files.readString(filePath);
    }
}
