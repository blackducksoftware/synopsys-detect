/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.common.util.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringTokenizer;
import org.apache.commons.text.matcher.StringMatcherFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.util.Stringable;

public class CommandParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final char SINGLE_QUOTE_CHAR = '\'';
    private static final char DOUBLE_QUOTE_CHAR = '"';
    private static final char ESCAPE_CHAR = '\\';
    private static final char TAB_CHAR = '\t';
    private static final char SPACE_CHAR = ' ';
    private static final String SPACE_CHAR_AS_STRING = " ";
    private static final String TAB_CHAR_AS_STRING = "\t";
    private static final String ENCODED_SPACE_CHAR = "%20";
    private static final String ENCODED_TAB_CHAR = "%09";

    public List<String> parseCommandString(String commandString) {
        return parseCommandString(commandString, new HashMap<>());
    }

    public List<String> parseCommandString(String commandString, Map<String, String> optionOverrides) {
        logger.trace(String.format("origCommand         : %s", commandString));
        String quotesRemovedCommand = encodeQuotedWhitespace(commandString);
        logger.trace(String.format("quotesRemovedCommand: %s", quotesRemovedCommand));
        StringTokenizer tokenizer = new StringTokenizer(quotesRemovedCommand);
        tokenizer.setQuoteMatcher(StringMatcherFactory.INSTANCE.quoteMatcher());
        List<String> commandList = new ArrayList<>();
        String lastPart = "";
        int partIndex = 0;
        while (tokenizer.hasNext()) {
            String token = tokenizer.nextToken();
            String part = restoreWhitespace(token);
            if (partIndex > 0) {
                String optionValueOverride = null;
                for (Map.Entry<String, String> optionToOverride : optionOverrides.entrySet()) {
                    if (optionToOverride.getKey().equals(lastPart)) {
                        optionValueOverride = optionToOverride.getValue();
                    }
                }
                if (optionValueOverride != null) {
                    commandList.add(optionValueOverride);
                } else {
                    commandList.add(part);
                }
            } else {
                commandList.add(part);
            }
            lastPart = part;
            partIndex++;
        }
        return commandList;
    }

    private String restoreWhitespace(String givenString) {
        String newString = givenString
                               .replace(ENCODED_SPACE_CHAR, SPACE_CHAR_AS_STRING)
                               .replace(ENCODED_TAB_CHAR, TAB_CHAR_AS_STRING);
        logger.trace(String.format("restoreWhitespace() changed %s to %s", givenString, newString));
        return newString;
    }

    private String encodeQuotedWhitespace(String givenString) {
        StringBuilder newString = new StringBuilder();
        ParserState parserState = new ParserState();
        for (int i = 0; i < givenString.length(); i++) {
            char c = givenString.charAt(i);
            if (parserState.getQuoteType() != QuoteType.NONE) {
                processQuotedChar(parserState, c, newString);
            } else {
                processNonQuotedChar(parserState, c, newString);
            }
            parserState.setLastCharWasEscapeChar(c == ESCAPE_CHAR);
        }
        logger.trace(String.format("escapeQuotedWhitespace() changed %s to %s", givenString, newString.toString()));
        return newString.toString();
    }

    private void processQuotedChar(ParserState parserState, char c, StringBuilder newString) {
        // Currently inside a quoted substring
        if ((!parserState.isLastCharEscapeChar() && (c == SINGLE_QUOTE_CHAR) && (parserState.getQuoteType() == QuoteType.SINGLE)) ||
                (!parserState.isLastCharEscapeChar() && (c == DOUBLE_QUOTE_CHAR) && (parserState.getQuoteType() == QuoteType.DOUBLE)) ||
                (parserState.isLastCharEscapeChar() && (c == DOUBLE_QUOTE_CHAR) && parserState.getQuoteType() == QuoteType.ESCAPED_DOUBLE)) {
            // Close quote
            parserState.setQuoteType(QuoteType.NONE);
            newString.append(c);
        } else if (c == SPACE_CHAR) {
            newString.append(ENCODED_SPACE_CHAR);
        } else if (c == TAB_CHAR) {
            newString.append(ENCODED_TAB_CHAR);
        } else {
            newString.append(c);
        }
    }

    private void processNonQuotedChar(ParserState parserState, char c, StringBuilder newString) {
        if (!parserState.isLastCharEscapeChar() && (c == SINGLE_QUOTE_CHAR)) {
            parserState.setQuoteType(QuoteType.SINGLE);
            newString.append(c);
        } else if (!parserState.isLastCharEscapeChar() && (c == DOUBLE_QUOTE_CHAR)) {
            parserState.setQuoteType(QuoteType.DOUBLE);
            newString.append(c);
        } else if (parserState.isLastCharEscapeChar() && (c == DOUBLE_QUOTE_CHAR)) {
            parserState.setQuoteType(QuoteType.ESCAPED_DOUBLE);
            newString.append(c);
        } else {
            newString.append(c);
        }
    }

    private enum QuoteType {
        NONE,
        SINGLE,
        DOUBLE,
        ESCAPED_DOUBLE
    }

    private static class ParserState extends Stringable {
        private boolean lastCharWasEscapeChar = false;
        private QuoteType quoteType = QuoteType.NONE;

        public boolean isLastCharEscapeChar() {
            return lastCharWasEscapeChar;
        }

        public QuoteType getQuoteType() {
            return quoteType;
        }

        public void setLastCharWasEscapeChar(boolean lastCharWasEscapeChar) {
            this.lastCharWasEscapeChar = lastCharWasEscapeChar;
        }

        public void setQuoteType(QuoteType quoteType) {
            this.quoteType = quoteType;
        }
    }
}
