/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.report.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class StringManager {
    public static List<String> wrapToCombinedList(final PDFont font, final float fontSize, final String str, final float widthLimit) throws IOException {
        List<String> nonBlankWords = breakIntoWordsIfTooLong(font, fontSize, str, widthLimit);
        List<String> nonBlankTrimmedFinalStrings = recombineBrokenWordsIfPossible(font, fontSize, widthLimit, nonBlankWords);

        return nonBlankTrimmedFinalStrings;
    }

    private static List<String> breakIntoWordsIfTooLong(PDFont font, float fontSize, String str, float widthLimit) throws IOException {
        final List<String> words = new ArrayList<>(Arrays.asList(str.split(" ")));
        for (int i = 0; i < words.size(); i++) {
            final String word = words.get(i);
            final float stringWidth = getStringWidth(font, fontSize, word);
            if (stringWidth > widthLimit) {
                words.remove(word);
                final List<String> brokenStrings = breakWrapString(font, fontSize, word, widthLimit);
                words.addAll(i, brokenStrings);
                i = i + brokenStrings.size();
            }
        }

        return words
                   .stream()
                   .filter(StringUtils::isNotBlank)
                   .collect(Collectors.toList());
    }

    private static List<String> recombineBrokenWordsIfPossible(PDFont font, float fontSize, float widthLimit, List<String> nonBlankWords) throws IOException {
        List<String> finalStrings = new ArrayList<>();
        StringBuilder currentBuilder = new StringBuilder();
        for (final String word : nonBlankWords) {
            if (wouldExceedLimit(currentBuilder, word, font, fontSize, widthLimit)) {
                finalStrings.add(currentBuilder.toString());
                currentBuilder = new StringBuilder(word);
            } else {
                currentBuilder.append(" ");
                currentBuilder.append(word);
            }
        }

        finalStrings.add(currentBuilder.toString());

        return finalStrings
                   .stream()
                   .filter(StringUtils::isNotBlank)
                   .map(StringUtils::trim)
                   .collect(Collectors.toList());
    }

    private static boolean wouldExceedLimit(StringBuilder builder, String toAdd, final PDFont font, final float fontSize, float widthLimit) throws IOException {
        return getStringWidth(font, fontSize, builder.toString()) + getStringWidth(font, fontSize, toAdd) > widthLimit;
    }

    public static float getStringWidth(final PDFont font, final float fontSize, final String text) throws IOException {
        final String fixedText = replaceUnsupportedCharacters(text, font);
        final float rawLength = font.getStringWidth(fixedText);
        //TODO evaluate why we are not using 1000f???
        return rawLength * (fontSize / 960f);
    }

    public static List<String> breakWrapString(final PDFont font, final float fontSize, final String str, final float widthLimit) throws IOException {
        int lastBreak = 0;
        float maxLengthCounter = 0;
        final int strLen = str.length();
        final ArrayList<String> brokenUpStrings = new ArrayList<>();
        // break up strings on non alphanumeric IF POSSIBLE
        for (int i = 1; i < strLen; i++) {
            if (!StringUtils.isAlphanumeric(str.charAt(i) + "") || maxLengthCounter >= widthLimit) {
                brokenUpStrings.add(str.substring(lastBreak, i));
                lastBreak = i;
                maxLengthCounter = 0;
            } else {
                maxLengthCounter = getStringWidth(font, fontSize, str.substring(lastBreak, i));
            }
        }
        // add remaining string to the list so nothing gets lost
        if (brokenUpStrings.isEmpty() || (maxLengthCounter > 0 && maxLengthCounter < widthLimit)) {
            brokenUpStrings.add(str.substring(lastBreak, strLen));
        }

        final List<String> finalStrings = new ArrayList<>();
        String currentStringCombo = "";
        // combine broken pieces if they will fit within the limit
        for (final String currentBrokenString : brokenUpStrings) {
            if (getStringWidth(font, fontSize, currentStringCombo) + getStringWidth(font, fontSize, currentBrokenString) > widthLimit) {
                finalStrings.add(currentStringCombo);
                currentStringCombo = currentBrokenString;
            } else {
                currentStringCombo += currentBrokenString;
            }
        }
        if (currentStringCombo.length() > 0) {
            finalStrings.add(currentStringCombo);
        }

        return finalStrings;
    }

    public static String replaceUnsupportedCharacters(final String text, final PDFont font) {
        return replaceUnsupportedCharacters(text, Collections.singletonList(font));
    }

    public static String replaceUnsupportedCharacters(final String text, final List<PDFont> fonts) {
        String result = "";
        if (text.length() > 0) {
            for (int i = 0; i < text.length(); ) {
                final int codePoint = text.codePointAt(i);
                final int codeChars = Character.charCount(codePoint);
                final String codePointString = text.substring(i, i + codeChars);
                boolean canEncode = false;
                for (final PDFont font : fonts) {
                    try {
                        font.encode(codePointString);
                        canEncode = true;
                        break;
                    } catch (final Exception ioe) {
                        // Font cannot encode glyph. Glyph will be replaced
                    }
                }
                if (canEncode) {
                    result += codePointString;
                } else {
                    result += "?";
                }
                i += codeChars;
            }
        }
        return result;
    }

}
