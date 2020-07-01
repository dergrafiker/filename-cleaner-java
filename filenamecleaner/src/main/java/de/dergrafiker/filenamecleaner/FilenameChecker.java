package de.dergrafiker.filenamecleaner;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/*
    https://www.boost.org/doc/libs/1_73_0/libs/filesystem/doc/portability_guide.htm#recommendations

    Limit file and directory names to the characters A-Z, a-z, 0-9, period, hyphen, and underscore.
    Limit the length of the string returned by path::string() to 255 characters.
    */
@Component
public class FilenameChecker {
    private final MatcherUtil matcherUtil;

    public FilenameChecker(MatcherUtil matcherUtil) {
        this.matcherUtil = matcherUtil;
    }

    public boolean isInvalid(final String input, final boolean isDirectory) {
        return wrongStartOrEnd(input)
                || wrongDirectoryName(isDirectory, input)
                || wrongFilename(isDirectory, input)
                || hasInvalidChars(input);
    }

    boolean hasInvalidChars(final String input) {
        return matcherUtil.getMatcher(MatcherUtil.INVALID_CHARS_PATTERN, input).find();
    }

    boolean wrongFilename(boolean isDirectory, String input) {
        return !isDirectory && hasMoreThanOneDot(input);
    }

    boolean hasMoreThanOneDot(String input) {
        return StringUtils.countMatches(input, '.') > 1;
    }

    boolean wrongDirectoryName(final boolean isDirectory, final String input) {
        return isDirectory && input.contains(".");
    }

    boolean wrongStartOrEnd(final String input) {
        return input.startsWith("-")
                || input.startsWith(".")
                || input.endsWith(".");
    }
}
