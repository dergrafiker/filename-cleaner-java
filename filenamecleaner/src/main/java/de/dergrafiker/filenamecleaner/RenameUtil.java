package de.dergrafiker.filenamecleaner;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class RenameUtil {
    private final CaseSensivityChecker caseSensivityChecker;

    public RenameUtil(CaseSensivityChecker caseSensivityChecker) {
        this.caseSensivityChecker = caseSensivityChecker;
    }

    public void rename(Path source, Path target) throws IOException {
        if (source == null || target == null) {
            throw new IllegalArgumentException(
                    String.format("Both paths must not be null (source=%s, target=%s)", source, target));
        }

        if (!source.getParent().equals(target.getParent())) {
            throw new IllegalArgumentException(
                    String.format("Paths must have the same parent (source=%s, target=%s)", source, target));
        }

        if (Files.isSameFile(source, target)) {
            throw new IllegalArgumentException(
                    String.format("Paths must not point to the same file (source=%s, target=%s)", source, target));
        }
        Files.move(source, target);
    }
}
