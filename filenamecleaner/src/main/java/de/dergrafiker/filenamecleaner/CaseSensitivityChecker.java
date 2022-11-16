package de.dergrafiker.filenamecleaner;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CaseSensitivityChecker {

  private final Map<Path, Boolean> rootToCaseSensitivityMap = new HashMap<>();

  public boolean isCaseSensitive(Path path) {
    Path root = path.getRoot();

    if (root == null) {
      throw new IllegalArgumentException(String.format("Path %s must have a root", path));
    }

    return rootToCaseSensitivityMap.computeIfAbsent(root, this::lowerCaseAndUpperCaseAreNotEqual);
  }

  private boolean lowerCaseAndUpperCaseAreNotEqual(Path root) {
    String originalFilename = "test";
    Path lc = root.resolve(originalFilename.toLowerCase());
    Path uc = root.resolve(originalFilename.toUpperCase());
    return !lc.equals(uc);
  }
}
