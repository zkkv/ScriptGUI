package com.github.zkkv.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GUIService {

    public void saveCodeToFile(final Path savepath, final String code) throws IOException {
        Files.writeString(savepath, code);
    }
}
