package de.dergrafiker.filenamecleaner;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class CaseSensitivityCheckerTest {

    private final CaseSensitivityChecker caseSensitivityChecker = new CaseSensitivityChecker();

    @Test
    void whenNotOnWindowsThenFilenamesAreCaseSensitive() throws IOException {
        assumeFalse(SystemUtils.IS_OS_WINDOWS);
        Path tempFile = Files.createTempFile("foo", "bar");
        assertThat(caseSensitivityChecker.isCaseSensitive(tempFile)).isTrue();
    }

    @Test
    void whenOnWindowsThenFilenamsAreNotCaseSensitive() throws IOException {
        assumeTrue(SystemUtils.IS_OS_WINDOWS);
        Path tempFile = Files.createTempFile("foo", "bar");
        assertThat(caseSensitivityChecker.isCaseSensitive(tempFile)).isFalse();
    }
}