package com.github.zkkv;

public class Main {

    /**
     * Calls the main method of class of {@link EntryPoint} class.
     * They had to be separated because whenever {@code Main} extends {@code Application} class,
     * JavaFX fails to start properly.
     *
     * @param args CLI arguments
     */
    public static void main(final String[] args) {
        EntryPoint.main(args);
    }

}
