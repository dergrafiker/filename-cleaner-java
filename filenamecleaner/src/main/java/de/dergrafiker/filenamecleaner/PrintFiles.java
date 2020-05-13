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
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        final File file = dir.toFile();
        String oldName = file.getName();
        if (FilenameChecker.isInvalid(oldName, file.isDirectory())) {
            String cleaned = FilenameCleaner.clean(oldName, file.isDirectory());

            Set<Character> removedChars = getUniqueLowerCaseChars(oldName);
            removedChars.removeAll(getUniqueLowerCaseChars(cleaned));

            Set<Character> ignored = new HashSet<>();
            Collections.addAll(ignored, '[', ']', '(', ')', ',', ';', ' ', '.', '&', '+', '#', '$', '!', '\'');

            removedChars.removeAll(ignored);

            if (removedChars.size() > 0) {
                LOGGER.info("{} has removed >> {} << {}",
                            oldName,
                            StringUtils.join(removedChars),
                            cleaned);
            }

//            LOGGER.info("'{}' => '{}' [{}]",
//                        StringUtils.rightPad(oldName, 70, ' '),
//                        StringUtils.rightPad(cleaned, 70, ' '),
//                        file.getParentFile().getAbsolutePath());
        }
//        LOGGER.info("walked dir {}", dir);
        return CONTINUE;
    }

    private Set<Character> getUniqueLowerCaseChars(String oldName) {
        Stream<Character> characterStream = oldName.chars()
                .mapToObj(c -> (char) Character.toLowerCase(c));

        return characterStream.collect(Collectors.toSet());
    }
}
