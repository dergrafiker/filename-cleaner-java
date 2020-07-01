package de.dergrafiker.filenamecleaner;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.regex.Matcher;

@Component
public class FilenameCleaner {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilenameCleaner.class);

    private static final String[] SEARCH_UMLAUTS = {
            "\u00c4", "\u00e4",
            "\u00d6", "\u00f6",
            "\u00dc", "\u00fc",
            "\u00df"};
    private static final String[] REPLACE_UMLAUTS = {
            "Ae", "ae",
            "Oe", "oe",
            "Ue", "ue",
            "ss"};
    private static final String[] SEARCH_DASHES = {"_-_", "-_", "_-"};
    private static final String[] REPLACE_DASHES = {"-", "-", "-"};

    private final MatcherUtil matcherUtil;

    public FilenameCleaner(MatcherUtil matcherUtil) {
        this.matcherUtil = matcherUtil;
    }

    String clean(final String name, final boolean isDirectory) {
        String output = name.trim();

        if (isDirectory) {
            output = StringUtils.replaceChars(output, '.', ' ');
        }

        output = StringUtils.replaceEach(output, SEARCH_UMLAUTS, REPLACE_UMLAUTS);
        output = StringUtils.stripAccents(output);

        String normalized = Normalizer.normalize(output, Normalizer.Form.NFD);
        if (!normalized.equalsIgnoreCase(output)) {
            LOGGER.error("NORMALIZATION LOOKS FISHY {} -> {}", output, normalized);
        }

        output = StringUtils.replace(output, "&", " Et ");

        output = replaceUppercaseWords(output);
        output = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, output);

        output = matcherUtil.getMatcher(MatcherUtil.INVALID_CHARS_PATTERN, output).replaceAll(" ");
        output = output.trim();

        output = matcherUtil.getMatcher("\\s+", output).replaceAll("_");
        output = matcherUtil.getMatcher("_+", output).replaceAll("_");

        output = StringUtils.replaceEach(output, SEARCH_DASHES, REPLACE_DASHES);

        return output;
    }

    String replaceUppercaseWords(final String output) {
        String cleaned = output;

        final Matcher matcher = matcherUtil.getMatcher(MatcherUtil.MANY_UPPERCASE_PATTERN, output);

        while (matcher.find()) {
            String ucWord = matcher.group();
            String corrected = WordUtils.capitalizeFully(ucWord);
            cleaned = StringUtils.replaceOnce(cleaned, ucWord, corrected);
        }
        return cleaned;
    }
}
