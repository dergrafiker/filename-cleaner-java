package de.dergrafiker.filenamecleaner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;

import static java.nio.file.FileVisitResult.CONTINUE;

public class PrintFiles extends SimpleFileVisitor<Path> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        LOGGER.info("walked dir {}", dir);
        return CONTINUE;
    }
}
