package de.dergrafiker.filenamecleaner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final RemovedCharsUtil removedCharsUtil = new RemovedCharsUtil();

    public static void main(String[] args) {
        try {
            LOGGER.info("run main");
            if (args.length != 1) {
                throw new IllegalArgumentException("Please provide a root path");
            }
            final Path rootPath = Paths.get(args[0]);
            PrintFiles pf = new PrintFiles(removedCharsUtil);
            Files.walkFileTree(rootPath, pf);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }
}
