package de.dergrafiker.filenamecleaner;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class CaseSensivityChecker {

    public boolean isCaseSensitive(Path path) {
        String originalFilename = path.getFileName().toString();

        Path lc = path.getParent().resolve(originalFilename.toLowerCase());
        Path uc = path.getParent().resolve(originalFilename.toUpperCase());

        return !lc.equals(uc);
    }
}
