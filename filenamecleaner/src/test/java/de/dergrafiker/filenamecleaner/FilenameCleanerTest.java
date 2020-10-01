package de.dergrafiker.filenamecleaner;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FilenameCleanerTest {

    final FilenameCleaner cleaner = new FilenameCleaner(new MatcherUtil());

    @Test
    void name() {
//
        assertThat(cleaner.clean(".f.o.o.b.a.r.", true)).isEqualTo("foobar");
        assertThat(cleaner.clean(".f.o.o.....bar", false)).isEqualTo("foo.bar");

    }
}