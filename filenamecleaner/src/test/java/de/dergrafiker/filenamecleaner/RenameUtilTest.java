package de.dergrafiker.filenamecleaner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.easymock.EasyMock.expect;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.easymock.EasyMockExtension;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EasyMockExtension.class)
class RenameUtilTest extends EasyMockSupport {

    @Mock
    CaseSensitivityChecker caseSensitivityChecker;

    RenameUtil renameUtil;

    static final Set<Path> cleanupAfterRun = new HashSet<>();

    @BeforeEach
    void setUp() {
        renameUtil = new RenameUtil(caseSensitivityChecker);
    }

    @Test
    void whenRenameSucceedsThenFileHasTargetFilename() throws IOException {

        Path tempDirectory = Files.createTempDirectory("foo");
        cleanupAfterRun.add(tempDirectory);

        String sourceName = "foobar";
        Path lowerCaseFile = tempDirectory.resolve(sourceName.toLowerCase());
        Path upperCaseFile = tempDirectory.resolve(sourceName.toUpperCase());

        Files.createFile(lowerCaseFile);

        expect(caseSensitivityChecker.isCaseSensitive(upperCaseFile)).andReturn(false);

        replayAll();
        renameUtil.rename(lowerCaseFile, upperCaseFile);
        verifyAll();

        List<Path> results = Files.walk(tempDirectory)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
        assertThat(results).hasSize(1);

        String foundFilename = results.get(0).getFileName().toString();
        assertThat(foundFilename)
            .isEqualTo(upperCaseFile.getFileName().toString())
            .isNotEqualTo(lowerCaseFile.getFileName().toString());
    }

    @Test
    void whenRenameSucceedsThenDirectoryHasTargetFilename() throws IOException {

        Path tempDirectory = Files.createTempDirectory("foo");
        cleanupAfterRun.add(tempDirectory);

        String sourceName = "foobar";
        Path lowerCaseFile = tempDirectory.resolve(sourceName.toLowerCase());
        Path upperCaseFile = tempDirectory.resolve(sourceName.toUpperCase());

        Files.createDirectory(lowerCaseFile);

        expect(caseSensitivityChecker.isCaseSensitive(upperCaseFile)).andReturn(false);

        replayAll();
        renameUtil.rename(lowerCaseFile, upperCaseFile);
        verifyAll();

        List<Path> subFolder = Files.walk(tempDirectory, 1)
                .filter(Files::isDirectory)
                .collect(Collectors.toList());

        Path removed = subFolder.remove(0); //this should be the temp folder

        assertThat(removed).isEqualTo(tempDirectory);
        assertThat(subFolder).hasSize(1);

        String foundFilename = subFolder.get(0).getFileName().toString();
        assertThat(foundFilename)
            .isEqualTo(upperCaseFile.getFileName().toString())
            .isNotEqualTo(lowerCaseFile.getFileName().toString());
    }

    @Test
    void whenSourceIsNullThenExceptionIsThrown() throws IOException {
        Path tempDirectory = Files.createTempDirectory("foo");
        cleanupAfterRun.add(tempDirectory);

        assertThatThrownBy(() -> renameUtil.rename(null, tempDirectory))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Both paths must not be null");
    }

    @Test
    void whenTargetIsNullThenExceptionIsThrown() throws IOException {
        Path tempDirectory = Files.createTempDirectory("foo");
        cleanupAfterRun.add(tempDirectory);

        assertThatThrownBy(() -> renameUtil.rename(tempDirectory, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Both paths must not be null");
    }

    @Test
    void whenFilesHaveNotTheSameParentThenExceptionIsThrown() throws IOException {
        Path dir1 = Files.createTempDirectory("dir1");
        cleanupAfterRun.add(dir1);

        Path foobar1 = Files.createFile(dir1.resolve("foobar"));
        cleanupAfterRun.add(foobar1);

        Path dir2 = Files.createTempDirectory("dir2");
        cleanupAfterRun.add(dir2);

        Path foobar2 = Files.createFile(dir2.resolve("foobar"));
        cleanupAfterRun.add(foobar2);

        assertThatThrownBy(() -> renameUtil.rename(foobar1, foobar2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Paths must have the same parent");
    }

    @Test
    void whenDirectoriesHaveNotTheSameParentThenExceptionIsThrown() throws IOException {
        Path dir1 = Files.createTempDirectory("dir1");
        cleanupAfterRun.add(dir1);

        Path sub1 = Files.createDirectory(dir1.resolve("foobar"));
        cleanupAfterRun.add(sub1);

        Path dir2 = Files.createTempDirectory("dir2");
        cleanupAfterRun.add(dir2);

        Path sub2 = Files.createDirectory(dir2.resolve("foobar"));
        cleanupAfterRun.add(sub2);

        assertThatThrownBy(() -> renameUtil.rename(sub1, sub2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Paths must have the same parent");
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
                                //we really dont care
                            }
                        });
            } catch (IOException e) {
                //we really dont care
            }
        });

        cleanupAfterRun.forEach(path -> assertThat(path).doesNotExist());
    }
}