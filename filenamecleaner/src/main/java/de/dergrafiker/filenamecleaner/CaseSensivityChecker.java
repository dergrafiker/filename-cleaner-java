package de.dergrafiker.filenamecleaner;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
public class CaseSensivityChecker {

    private final Map<Path, Boolean> rootToCaseSensivityMap = new HashMap<>();

    public boolean isCaseSensitive(Path path) {
        Path root = path.getRoot();

        if (root == null) {
            throw new IllegalArgumentException(String.format("Path %s must have a root", path));
        }

        return rootToCaseSensivityMap.computeIfAbsent(root, this::icCi);
    }

    private boolean icCi(Path root) {
        String originalFilename = "test";
        Path lc = root.resolve(originalFilename.toLowerCase());
        Path uc = root.resolve(originalFilename.toUpperCase());
        return !lc.equals(uc);
    }
}
