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

    private static final Function<String, Matcher> COMPILEREGEX = s -> Pattern.compile(s).matcher("");

    Matcher getMatcher(final String regex, final String input) {
        return MATCHER_CACHE.computeIfAbsent(regex, COMPILEREGEX).reset(input);
    }
}
