package de.dergrafiker.filenamecleaner;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.text.Normalizer;
import java.util.regex.Matcher;


public class FilenameCleaner {

    public static final String[] SEARCH_UMLAUTS = {
            "\u00c4", "\u00e4",
            "\u00d6", "\u00f6",
            "\u00dc", "\u00fc",
            "\u00df"};
    public static final String[] REPLACE_UMLAUTS = {
            "Ae", "ae",
            "Oe", "oe",
            "Ue", "ue",
            "ss"};
    public static final String[] SEARCH_DASHES = {"_-_", "-_", "_-"};
    public static final String[] REPLACE_DASHES = {"-", "-", "-"};

    static String clean(final String name, final boolean isDirectory) {
        String output = name.trim();

        if (isDirectory) {
            output = StringUtils.replaceChars(output, '.', ' ');
        }

        output = StringUtils.replaceEach(output, SEARCH_UMLAUTS, REPLACE_UMLAUTS);
        output = StringUtils.stripAccents(output);

        if (!Normalizer.normalize(output, Normalizer.Form.NFD).equalsIgnoreCase(output)) {
            System.out.println();
        }

        output = StringUtils.replace(output, "&", " Et ");

        output = replaceUppercaseWords(output);
        output = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, output);

        output = MatcherUtil.getMatcher(RegexConstants.INVALIDCHARS, output).replaceAll(" ");
        output = output.trim();

        output = MatcherUtil.getMatcher("\\s+", output).replaceAll("_");
        output = MatcherUtil.getMatcher("_+", output).replaceAll("_");

        output = StringUtils.replaceEach(output, SEARCH_DASHES, REPLACE_DASHES);

        return output;
    }

    static String replaceUppercaseWords(final String output) {
        String cleaned = output;

        final Matcher matcher = MatcherUtil.getMatcher(RegexConstants.MANYUPPERCASE, output);

        while (matcher.find()) {
            String ucWord = matcher.group();
            String corrected = WordUtils.capitalizeFully(ucWord);
            cleaned = StringUtils.replaceOnce(cleaned, ucWord, corrected);
        }
        return cleaned;
    }
}
