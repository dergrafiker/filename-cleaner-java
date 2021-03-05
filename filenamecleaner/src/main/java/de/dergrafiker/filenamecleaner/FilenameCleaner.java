package de.dergrafiker.filenamecleaner;

import com.google.common.base.CaseFormat;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

@Component
public class FilenameCleaner {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilenameCleaner.class);

    private static final String[] SEARCH_UMLAUTS = {
            "\u00c4", "\u00e4",
            "\u00d6", "\u00f6",
            "\u00dc", "\u00fc",
            "\u00df", "\u1E9E"};
    private static final String[] REPLACE_UMLAUTS = {
            "Ae", "ae",
            "Oe", "oe",
            "Ue", "ue",
            "ss", "ss"};
    private static final String[] SEARCH_DASHES = {"_-_", "-_", "_-"}; //can be replaced by regex [_-]+
    private static final String[] REPLACE_DASHES = {"-", "-", "-"};

    String clean(final String name, final boolean isDirectory) {
        String output = name.trim();
        output = StringEscapeUtils.unescapeHtml4(output);

        output = StringUtils.removeStart(output, ".");
        output = StringUtils.removeEnd(output, ".");
        output = StringUtils.removeStart(output, "-");

        output = replaceUppercaseWords(output);
        output = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, output);

        if (isDirectory) {
            output = StringUtils.replaceChars(output, '.', ' ');
        } else {
            if (StringUtils.countMatches(output, '.') > 1) {
                String baseName = FilenameUtils.getBaseName(output);
                baseName = StringUtils.replaceChars(baseName, '.', ' ');

                String extension = FilenameUtils.getExtension(output);
                output = baseName + '.' + extension;
            }
        }

//        output = StringEscapeUtils.unescapeHtml4(output);

//        output = MatcherUtil.getMatcher(MatcherUtil.INVALID_CHARS_PATTERN, output).replaceAll(" ");

        output = StringUtils.replaceChars(output, "'", " ");
        output = StringUtils.replaceChars(output, ",", " ");
        output = StringUtils.replaceChars(output, "`", " ");
        output = StringUtils.replaceChars(output, "[", " ");
        output = StringUtils.replaceChars(output, "]", " ");
        output = StringUtils.replaceChars(output, "(", " ");
        output = StringUtils.replaceChars(output, ")", " ");
        output = StringUtils.replaceChars(output, "{", " ");
        output = StringUtils.replaceChars(output, "}", " ");
        output = StringUtils.replaceChars(output, "+", " ");
        output = StringUtils.replaceChars(output, "=", " ");
        output = StringUtils.replaceChars(output, "!", " ");
        output = StringUtils.replaceChars(output, "#", " ");
        output = StringUtils.replaceChars(output, "’", " ");
        output = StringUtils.replaceChars(output, ";", " ");
        output = StringUtils.replaceChars(output, "%", " ");
        output = StringUtils.replaceChars(output, "•", " ");
        output = StringUtils.replaceChars(output, "♥", " ");
        output = StringUtils.replaceChars(output, "◕", " ");
        output = StringUtils.replaceChars(output, "°", " ");
        output = StringUtils.replaceChars(output, "Ж", " ");
        output = StringUtils.replaceChars(output, "Ф", " ");
        output = StringUtils.replaceChars(output, "Д", " ");
        output = StringUtils.replaceChars(output, "~", " ");
        output = StringUtils.replaceChars(output, "¡", " ");
        output = StringUtils.replaceChars(output, "$", " ");
        output = StringUtils.replaceChars(output, "…", " ");
        output = StringUtils.replaceChars(output, "^", " ");
        output = StringUtils.replaceChars(output, "@", " ");
        output = StringUtils.replaceChars(output, "´", " ");
        output = StringUtils.replaceChars(output, "„", " ");
        output = StringUtils.replaceChars(output, "“", " ");
        output = StringUtils.replaceChars(output, "£", " ");
        output = StringUtils.replaceChars(output, "∞", " ");

        output = StringUtils.replaceIgnoreCase(output, "&", "et");
        output = StringUtils.replaceIgnoreCase(output, "µ", "u");
        output = StringUtils.replaceIgnoreCase(output, "þ", "th");
        output = StringUtils.replaceIgnoreCase(output, "ß", "ss");

        output = StringUtils.replaceIgnoreCase(output, "­", "-");
        output = StringUtils.replaceIgnoreCase(output, "–", "-");
        output = StringUtils.replaceIgnoreCase(output, "‐", "-");

        output = StringUtils.replaceIgnoreCase(output, "ø", "oe");
        output = StringUtils.replaceIgnoreCase(output, "œ", "oe");
        output = StringUtils.replaceIgnoreCase(output, "æ", "ae");
        output = StringUtils.replaceIgnoreCase(output, "Æ", "ae");
        output = StringUtils.replaceIgnoreCase(output, "Ø", "O");
        output = StringUtils.replaceIgnoreCase(output, "ӱ", "ue");

        output = StringUtils.replaceEach(output, SEARCH_UMLAUTS, REPLACE_UMLAUTS);
        output = StringUtils.stripAccents(output);

        output = output.trim();

        output = MatcherUtil.getMatcher("\\s+", output).replaceAll("_");
        output = MatcherUtil.getMatcher("_+", output).replaceAll("_");

        output = StringUtils.replaceEach(output, SEARCH_DASHES, REPLACE_DASHES);


/*        output = removeDots(isDirectory, output);

        output = StringUtils.replaceEach(output, SEARCH_UMLAUTS, REPLACE_UMLAUTS);
        output = StringUtils.stripAccents(output);

        output = StringUtils.replace(output, "&", " Et ");

        output = replaceUppercaseWords(output);
        output = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, output);

        output = matcherUtil.getMatcher(MatcherUtil.INVALID_CHARS_PATTERN, output).replaceAll(" ");
        output = output.trim();

        output = matcherUtil.getMatcher("\\s+", output).replaceAll("_");
        output = matcherUtil.getMatcher("_+", output).replaceAll("_");

        output = StringUtils.replaceEach(output, SEARCH_DASHES, REPLACE_DASHES);
        output = StringUtils.replace(output, "_.", ".");

        if (output.startsWith(".")) {
            output = StringUtils.removeStart(output, ".");
        }

        if (output.startsWith("-")) {
            output = StringUtils.removeStart(output, "-");
        }

        if (output.endsWith(".")) {
            output = StringUtils.removeEnd(output, ".");
        }*/

        return output;
    }

    private String removeDots(boolean isDirectory, String toClean) {
        if (isDirectory && StringUtils.contains(toClean, '.')) {
            return StringUtils.remove(toClean, '.');
        } else if (StringUtils.countMatches(toClean, '.') > 1) {
            String extension = FilenameUtils.getExtension(toClean);
            String nameWithoutExtensionAndDots = StringUtils.remove(FilenameUtils.removeExtension(toClean), '.');
            String newOutput = nameWithoutExtensionAndDots + '.' + extension;

            LOGGER.info("Found too many dots in file. Renamed {} to {}", toClean, newOutput);
            return newOutput;
        } else {
            return toClean;
        }
    }

    String replaceUppercaseWords(final String output) {
        String cleaned = output;

        final Matcher matcher = MatcherUtil.getMatcher(MatcherUtil.MANY_UPPERCASE_PATTERN, output);

        while (matcher.find()) {
            String ucWord = matcher.group();
            String corrected = WordUtils.capitalizeFully(ucWord);
            cleaned = StringUtils.replaceOnce(cleaned, ucWord, corrected);
        }
        return cleaned;
    }
}
