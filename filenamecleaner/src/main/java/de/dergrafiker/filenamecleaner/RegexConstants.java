package de.dergrafiker.filenamecleaner;

public class RegexConstants {
    private RegexConstants() {
    }

    static final String INVALID_CHARS = "[^-_.A-Za-z0-9]+";
    static final String MANY_UPPERCASE = "[A-Z]{2,}";
}
