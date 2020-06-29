package de.dergrafiker.filenamecleaner;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class CaseSensivityChecker {

    public boolean isCaseSensitive(Path path) throws IOException {
        if (!Files.isWritable(path)) {
            throw new IllegalArgumentException(
                    String.format("Paths must be writable to test for case sensivity (path=%s)", path));
        }

        Path pathToWriteTo = getDir(path);

        String randomName = RandomStringUtils.randomAlphabetic(10);
        Path lowerCase = pathToWriteTo.resolve(randomName.toLowerCase());
        Path upperCase = pathToWriteTo.resolve(randomName.toUpperCase());

        if (Files.exists(lowerCase) || Files.exists(upperCase)) {
            throw new IllegalArgumentException(
                    String.format("One Filename already exists in target.(lowerCase=%s, upperCase=%s)",
                                  lowerCase, upperCase));
        }

        boolean isCaseSensitive;
        try {
            Files.createFile(lowerCase);
            isCaseSensitive = !Files.exists(upperCase);
        } finally {
            Files.deleteIfExists(lowerCase);
        }
        return isCaseSensitive;
    }

    private Path getDir(Path path) {
        if (Files.isDirectory(path)) {
            return path;
        } else {
            return path.getParent();
        }
    }

}
