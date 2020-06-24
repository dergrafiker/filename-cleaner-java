package de.dergrafiker.filenamecleaner;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/*
    http://www.boost.org/doc/libs/1_65_1/libs/filesystem/doc/portability_guide.htm#recommendations

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
                || wrongDirectoryname(isDirectory, input)
                || wrongFilename(isDirectory, input)
                || hasInvalidChars(input);
    }

    boolean hasInvalidChars(final String input) {
        return matcherUtil.getMatcher(RegexConstants.INVALIDCHARS, input).find();
    }

    boolean wrongFilename(boolean isDirectory, String input) {
        return !isDirectory && (hasMoreThanOneDot(input) || extensionIsTooLong(input));
    }

    boolean extensionIsTooLong(String input) {
        return StringUtils.substringAfter(input, ".").length() > 3;
    }

    boolean hasMoreThanOneDot(String input) {
        return StringUtils.countMatches(input, '.') > 1;
    }

    boolean wrongDirectoryname(final boolean isDirectory, final String input) {
        return isDirectory && input.contains(".");
    }

    boolean wrongStartOrEnd(final String input) {
        return input.startsWith("-")
                || input.startsWith(".")
                || input.endsWith(".");
    }
}
