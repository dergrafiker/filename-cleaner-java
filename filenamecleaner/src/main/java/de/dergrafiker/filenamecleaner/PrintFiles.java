package de.dergrafiker.filenamecleaner;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.FileVisitResult.CONTINUE;

public class PrintFiles extends SimpleFileVisitor<Path> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrintFiles.class);
    private static final Set<Character> IGNORED = fill();

    private static Set<Character> fill() {
        Set<Character> ignored = new HashSet<>();
        Collections.addAll(ignored, '[', ']', '(', ')', ',', ';', ' ', '.', '&', '+', '#', '$', '!', '\'');
        return ignored;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (exc != null) {
            throw exc;
        }

        final File file = dir.toFile();
        String oldName = file.getName();
        if (FilenameChecker.isInvalid(oldName, file.isDirectory())) {
            String cleaned = FilenameCleaner.clean(oldName, file.isDirectory());

            Set<Character> removedChars = getRemovedChars(oldName, cleaned);

            if (!removedChars.isEmpty() && LOGGER.isInfoEnabled()) {
                LOGGER.info("REMOVEDCHARS {} has removed >> {} << {}",
                            oldName,
                            StringUtils.join(removedChars),
                            cleaned);
            }

            LOGGER.info("RENAME '{}' => '{}' [{}]",
                        oldName,
                        cleaned,
                        file.getParentFile().getAbsolutePath());
        }
        LOGGER.info("WALKED dir {}", dir);
        return CONTINUE;
    }

    private Set<Character> getRemovedChars(String oldName, String cleaned) {
        Set<Character> removedChars = getUniqueLowerCaseChars(oldName);
        removedChars.removeAll(getUniqueLowerCaseChars(cleaned));
        removedChars.removeAll(IGNORED);
        return removedChars;
    }

    private Set<Character> getUniqueLowerCaseChars(String oldName) {
        Stream<Character> characterStream = oldName.chars()
                .mapToObj(c -> (char) Character.toLowerCase(c));

        return characterStream.collect(Collectors.toSet());
    }
}
