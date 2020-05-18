package de.dergrafiker.filenamecleaner;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.Set;

import static java.nio.file.FileVisitResult.CONTINUE;

public class PrintFiles extends SimpleFileVisitor<Path> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrintFiles.class);
    private final RemovedCharsUtil removedCharsUtil;

    public PrintFiles(RemovedCharsUtil removedCharsUtil) {
        this.removedCharsUtil = removedCharsUtil;
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

            Set<Character> removedChars = removedCharsUtil.getRemovedChars(oldName, cleaned);

            if (LOGGER.isInfoEnabled() && !removedChars.isEmpty()) {
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


}
