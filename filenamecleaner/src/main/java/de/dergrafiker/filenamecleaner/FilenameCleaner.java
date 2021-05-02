package de.dergrafiker.filenamecleaner;

import com.google.common.base.CaseFormat;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

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
                baseName = StringUtils.replaceChars(baseName, '.', ' ').trim();

                String extension = FilenameUtils.getExtension(output);
                output = baseName + '.' + extension;
            }
        }

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

        output = StringUtils.replace(output, "&", "et");
        output = StringUtils.replace(output, "µ", "u");
        output = StringUtils.replace(output, "þ", "th");
        output = StringUtils.replace(output, "ß", "ss");

        output = StringUtils.replace(output, "­", "-");
        output = StringUtils.replace(output, "–", "-");
        output = StringUtils.replace(output, "‐", "-");

        output = StringUtils.replace(output, "ø", "oe");
        output = StringUtils.replace(output, "œ", "oe");
        output = StringUtils.replace(output, "æ", "ae");
        output = StringUtils.replace(output, "Æ", "ae");
        output = StringUtils.replace(output, "Ø", "O");
        output = StringUtils.replace(output, "ӱ", "ue");
        output = StringUtils.replace(output, "ë", "e");
        output = StringUtils.replace(output, "ĝ", "g");
        output = StringUtils.replace(output, "å", "a");
        output = StringUtils.replace(output, "é", "e");
        output = StringUtils.replace(output, "ï", "i");
        output = StringUtils.replace(output, "í", "i");
        output = StringUtils.replace(output, "ñ", "n");
        output = StringUtils.replace(output, "ç", "c");
        output = StringUtils.replace(output, "ã", "a");
        output = StringUtils.replace(output, "Á", "A");
        output = StringUtils.replace(output, "Â", "A");
        output = StringUtils.replace(output, "à", "a");
        output = StringUtils.replace(output, "è", "e");
        output = StringUtils.replace(output, "ó", "o");

        output = StringUtils.replaceEach(output, SEARCH_UMLAUTS, REPLACE_UMLAUTS);

        output = output.trim();

        output = MatcherUtil.getMatcher("\\s+", output).replaceAll("_");
        output = MatcherUtil.getMatcher("_+", output).replaceAll("_");

        output = StringUtils.replaceEach(output, SEARCH_DASHES, REPLACE_DASHES);

        return output;
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
