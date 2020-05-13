package de.dergrafiker.filenamecleaner;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherUtil {
    private static Map<String, Matcher> MATCHER_CACHE = new HashMap<>();

    private static final Function<String, Matcher> COMPILEREGEX = s -> Pattern.compile(s).matcher("");

    static Matcher getMatcher(final String regex, final String input) {
        return MATCHER_CACHE.computeIfAbsent(regex, COMPILEREGEX).reset(input);
    }
}
