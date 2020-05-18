package de.dergrafiker.filenamecleaner;

import org.apache.commons.lang3.StringUtils;

/*
    http://www.boost.org/doc/libs/1_65_1/libs/filesystem/doc/portability_guide.htm#recommendations

    Limit file and directory names to the characters A-Z, a-z, 0-9, period, hyphen, and underscore.
    Limit the length of the string returned by path::string() to 255 characters.
    */
public class FilenameChecker {

    private FilenameChecker() {
    }

    public static boolean isInvalid(final String input, final boolean isDirectory) {
        return wrongStartOrEnd(input)
                || wrongDirectoryname(isDirectory, input)
                || wrongFilename(isDirectory, input)
                || hasInvalidChars(input);
    }

    static boolean hasInvalidChars(final String input) {
        return MatcherUtil.getMatcher(RegexConstants.INVALIDCHARS, input).find();
    }

    static boolean wrongFilename(boolean isDirectory, String input) {
        return !isDirectory && (hasMoreThanOneDot(input) || extensionIsTooLong(input));
    }

    static boolean extensionIsTooLong(String input) {
        return StringUtils.substringAfter(input, ".").length() > 3;
    }

    static boolean hasMoreThanOneDot(String input) {
        return StringUtils.countMatches(input, '.') > 1;
    }

    static boolean wrongDirectoryname(final boolean isDirectory, final String input) {
        return isDirectory && input.contains(".");
    }

    static boolean wrongStartOrEnd(final String input) {
        return input.startsWith("-")
                || input.startsWith(".")
                || input.endsWith(".");
    }
}
