package de.dergrafiker.filenamecleaner;

import com.google.common.base.CaseFormat;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

import static de.dergrafiker.filenamecleaner.MatcherUtil.getMatcher;
import static org.apache.commons.lang3.StringUtils.countMatches;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.removeStart;
import static org.apache.commons.lang3.StringUtils.replace;
import static org.apache.commons.lang3.StringUtils.replaceChars;
import static org.apache.commons.lang3.StringUtils.replaceEach;
import static org.apache.commons.lang3.StringUtils.replaceOnce;

@Component
public class FilenameCleaner {
    private static final String[] SEARCH_UMLAUTS = {
            "Ä", "ä",
            "Ö", "ö",
            "Ü", "ü",
            "ß", "ẞ"};
    private static final String[] REPLACE_UMLAUTS = {
            "Ae", "ae",
            "Oe", "oe",
            "Ue", "ue",
            "ss", "ss"};
    private static final String[] SEARCH_DASHES = {"_-_", "-_", "_-"}; //can be replaced by regex [_-]+
    private static final String[] REPLACE_DASHES = {"-", "-", "-"};
    private static final String TWO_OR_MORE_UPPERCASE_LETTERS = "[A-Z]{2,}";

    String clean(final String name, final boolean isDirectory) {
        String output = name.trim();
        output = StringEscapeUtils.unescapeHtml4(output);

        output = removeStart(output, ".");
        output = removeEnd(output, ".");
        output = removeStart(output, "-");

        output = replaceUppercaseWords(output);
        output = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, output);

        if (isDirectory) {
            output = replaceChars(output, '.', ' ');
        } else {
            if (countMatches(output, '.') > 1) {
                String baseName = FilenameUtils.getBaseName(output);
                baseName = replaceChars(baseName, '.', ' ').trim();

                String extension = FilenameUtils.getExtension(output);
                output = baseName + '.' + extension;
            }
        }

        output = replaceChars(output, "'", " ");
        output = replaceChars(output, ",", " ");
        output = replaceChars(output, "`", " ");
        output = replaceChars(output, "[", " ");
        output = replaceChars(output, "]", " ");
        output = replaceChars(output, "(", " ");
        output = replaceChars(output, ")", " ");
        output = replaceChars(output, "{", " ");
        output = replaceChars(output, "}", " ");
        output = replaceChars(output, "+", " ");
        output = replaceChars(output, "=", " ");
        output = replaceChars(output, "!", " ");
        output = replaceChars(output, "#", " ");
        output = replaceChars(output, "’", " ");
        output = replaceChars(output, ";", " ");
        output = replaceChars(output, "%", " ");
        output = replaceChars(output, "•", " ");
        output = replaceChars(output, "♥", " ");
        output = replaceChars(output, "◕", " ");
        output = replaceChars(output, "°", " ");
        output = replaceChars(output, "Ж", " ");
        output = replaceChars(output, "Ф", " ");
        output = replaceChars(output, "Д", " ");
        output = replaceChars(output, "~", " ");
        output = replaceChars(output, "¡", " ");
        output = replaceChars(output, "$", " ");
        output = replaceChars(output, "…", " ");
        output = replaceChars(output, "^", " ");
        output = replaceChars(output, "@", " ");
        output = replaceChars(output, "´", " ");
        output = replaceChars(output, "„", " ");
        output = replaceChars(output, "“", " ");
        output = replaceChars(output, "£", " ");
        output = replaceChars(output, "∞", " ");

        output = replace(output, "&", "et");
        output = replace(output, "µ", "u");
        output = replace(output, "þ", "th");
        output = replace(output, "ß", "ss");

        output = replace(output, "­", "-");
        output = replace(output, "–", "-");
        output = replace(output, "‐", "-");

        output = replace(output, "ø", "oe");
        output = replace(output, "œ", "oe");
        output = replace(output, "æ", "ae");
        output = replace(output, "Æ", "ae");
        output = replace(output, "Ø", "O");
        output = replace(output, "ӱ", "ue");
        output = replace(output, "ë", "e");
        output = replace(output, "ĝ", "g");
        output = replace(output, "å", "a");
        output = replace(output, "é", "e");
        output = replace(output, "é", "e");
        output = replace(output, "ï", "i");
        output = replace(output, "í", "i");
        output = replace(output, "ñ", "n");
        output = replace(output, "ç", "c");
        output = replace(output, "ã", "a");
        output = replace(output, "Á", "A");
        output = replace(output, "Â", "A");
        output = replace(output, "à", "a");
        output = replace(output, "è", "e");
        output = replace(output, "ó", "o");
        output = replace(output, "π", "pi");

        output = replaceEach(output, SEARCH_UMLAUTS, REPLACE_UMLAUTS);

        output = output.trim();

        output = getMatcher("\\s+", output).replaceAll("_");
        output = getMatcher("_+", output).replaceAll("_");

        output = replaceEach(output, SEARCH_DASHES, REPLACE_DASHES);

        return output;
    }

    String replaceUppercaseWords(final String output) {
        String cleaned = output;

        final Matcher matcher = getMatcher(TWO_OR_MORE_UPPERCASE_LETTERS, output);

        while (matcher.find()) {
            String ucWord = matcher.group();
            String corrected = WordUtils.capitalizeFully(ucWord);
            cleaned = replaceOnce(cleaned, ucWord, corrected);
        }
        return cleaned;
    }
}
