package de.dergrafiker.filenamecleaner;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MatcherUtil {
    private static final Map<String, Matcher> MATCHER_CACHE = new HashMap<>();

    private static final Function<String, Matcher> COMPILED_REGULAR_EXPRESSIONS = s -> Pattern.compile(s).matcher("");

    static final String INVALID_CHARS_PATTERN = "[^-_.A-Za-z0-9]+";
    static final String MANY_UPPERCASE_PATTERN = "[A-Z]{2,}";

    Matcher getMatcher(final String regex, final String input) {
        return MATCHER_CACHE.computeIfAbsent(regex, COMPILED_REGULAR_EXPRESSIONS).reset(input);
    }
}
