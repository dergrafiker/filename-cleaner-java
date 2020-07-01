package de.dergrafiker.filenamecleaner;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class CaseSensitivityCheckerTest {

    private final CaseSensitivityChecker caseSensitivityChecker = new CaseSensitivityChecker();

    @Test
    void name1() throws IOException {
        assumeFalse(SystemUtils.IS_OS_WINDOWS);
        Path tempFile = Files.createTempFile("foo", "bar");
        assertThat(caseSensitivityChecker.isCaseSensitive(tempFile)).isTrue();
    }
}