package de.dergrafiker.filenamecleaner;

import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class CaseSensivityChecker {

    public boolean isCaseSensitive(Path path) {
        return false;
    }

}
