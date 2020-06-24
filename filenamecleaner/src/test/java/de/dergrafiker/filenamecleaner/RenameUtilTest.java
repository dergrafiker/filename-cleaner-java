package de.dergrafiker.filenamecleaner;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class RenameUtilTest {

    RenameUtil renameUtil = new RenameUtil();

    static Set<Path> cleanupAfterRun = new HashSet<>();

    @Test
    void name() throws IOException {

        Path tempDirectory = Files.createTempDirectory("foo");
        cleanupAfterRun.add(tempDirectory);

        String sourceName = "foobar";
        Path source = tempDirectory.resolve(sourceName.toLowerCase());
        Files.createFile(source);

        String targetName = sourceName.toUpperCase();
        Path target = tempDirectory.resolve(targetName);

        renameUtil.rename(source, target);

        List<Path> results = Files.walk(tempDirectory).collect(Collectors.toList());
        assertThat(results).hasSize(1);
        assertThat(results.get(0).toFile().getName()).isEqualTo(targetName);
    }

    @AfterAll
    static void afterAll() {
        cleanupAfterRun.forEach(path -> {
            try {
                Files.walk(path)
                        .sorted(Comparator.reverseOrder())
                        .forEach(child -> {
                            try {
                                Files.delete(child);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        cleanupAfterRun.forEach(path -> assertThat(path).doesNotExist());
    }
}