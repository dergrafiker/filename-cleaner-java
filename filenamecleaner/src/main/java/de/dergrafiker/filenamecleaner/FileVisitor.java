package de.dergrafiker.filenamecleaner;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;

import static java.nio.file.FileVisitResult.CONTINUE;

@Component
public class FileVisitor extends SimpleFileVisitor<Path> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileVisitor.class);
    private final FilenameChecker filenameChecker;
    private final FilenameCleaner filenameCleaner;


    public FileVisitor(FilenameChecker filenameChecker,
                       FilenameCleaner filenameCleaner) {
        this.filenameChecker = filenameChecker;
        this.filenameCleaner = filenameCleaner;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (exc != null) {
            throw exc;
        }

        fixName(dir);
        LOGGER.trace("WALKED dir {}", dir);
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        fixName(file);
        return CONTINUE;
    }

    private void fixName(Path path) {
        String oldName = path.getFileName().toString();
        boolean isDirectory = Files.isDirectory(path);

        if (filenameChecker.isInvalid(oldName, isDirectory)) {
            String cleaned = filenameCleaner.clean(oldName, isDirectory);

            if (filenameChecker.isInvalid(cleaned, isDirectory)) {
                throw new IllegalArgumentException(
                        String.format("Name is still invalid after clean '%s' => '%s' [%s]",
                                      oldName,
                                      cleaned,
                                      path.getParent().toAbsolutePath())
                );
            }

            LOGGER.info("RENAME '{}' => '{}' [{}]",
                        oldName,
                        cleaned,
                        path.getParent().toAbsolutePath()
            );
        }
    }
}
