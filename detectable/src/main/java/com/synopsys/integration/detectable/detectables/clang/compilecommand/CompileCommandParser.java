/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detectable.detectables.clang.compilecommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringTokenizer;
import org.apache.commons.text.matcher.StringMatcherFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompileCommandParser {
    private static final String ESCAPED_DOUBLE_QUOTE = "\\\\\"";
    private static final String DOUBLE_QUOTE = "\"";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final char SINGLE_QUOTE_CHAR = '\'';
    private static final char DOUBLE_QUOTE_CHAR = '"';
    private static final char ESCAPE_CHAR = '\\';
    private static final char TAB_CHAR = '\t';
    private static final char SPACE_CHAR = ' ';
    private static final String SPACE_CHAR_AS_STRING = " ";
    private static final String TAB_CHAR_AS_STRING = "\t";
    private static final String ESCAPE_SEQUENCE_FOR_SPACE_CHAR = "%20";
    private static final String ESCAPE_SEQUENCE_FOR_TAB_CHAR = "%09";

    public List<String> parseCommand(final CompileCommand compileCommand, final Map<String, String> optionOverrides) {

        String commandString = compileCommand.command;
        if (StringUtils.isBlank(commandString)) {
            commandString = String.join(" ", compileCommand.arguments);
        }
        final List<String> commandList = parseCommandString(commandString, optionOverrides);
        return commandList;
    }

    public List<String> parseCommandString(final String commandString, final Map<String, String> optionOverrides) {
        logger.trace(String.format("origCompileCommand         : %s", commandString));
        final String quotesRemovedCompileCommand = escapeQuotedWhitespace(commandString);
        logger.trace(String.format("quotesRemovedCompileCommand: %s", quotesRemovedCompileCommand));
        final StringTokenizer tokenizer = new StringTokenizer(quotesRemovedCompileCommand);
        tokenizer.setQuoteMatcher(StringMatcherFactory.INSTANCE.quoteMatcher());
        final List<String> commandList = new ArrayList<>();
        String lastPart = "";
        int partIndex = 0;
        while (tokenizer.hasNext()) {
            final String part = unEscapeDoubleQuotes(restoreWhitespace(tokenizer.nextToken()));
            if (partIndex > 0) {
                String optionValueOverride = null;
                for (final String optionToOverride : optionOverrides.keySet()) {
                    if (optionToOverride.equals(lastPart)) {
                        optionValueOverride = optionOverrides.get(optionToOverride);
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

    private String restoreWhitespace(final String givenString) {
        final String newString = givenString
                                     .replace(ESCAPE_SEQUENCE_FOR_SPACE_CHAR, SPACE_CHAR_AS_STRING)
                                     .replace(ESCAPE_SEQUENCE_FOR_TAB_CHAR, TAB_CHAR_AS_STRING);
        logger.trace(String.format("restoreWhitespace() changed %s to %s", givenString, newString));
        return newString;
    }

    private String unEscapeDoubleQuotes(final String givenString) {
        final String newString = givenString.replaceAll(ESCAPED_DOUBLE_QUOTE, DOUBLE_QUOTE);
        logger.trace(String.format("unEscapeDoubleQuotes() changed %s to %s", givenString, newString));
        return newString;
    }

    private String escapeQuotedWhitespace(final String givenString) {
        final StringBuilder newString = new StringBuilder();
        final ParserState parserState = new ParserState();
        for (int i = 0; i < givenString.length(); i++) {
            final char c = givenString.charAt(i);
            if (parserState.isInQuotes()) {
                processQuotedChar(parserState, c, newString);
            } else {
                processNonQuotedChar(parserState, c, newString);
            }
            parserState.setLastCharWasEscapeChar(c == ESCAPE_CHAR);
        }
        logger.trace(String.format("escapeQuotedWhitespace() changed %s to %s", givenString, newString.toString()));
        return newString.toString();
    }

    private void processQuotedChar(final ParserState parserState, final char c, final StringBuilder newString) {
        // Currently inside a quoted substring
        if (!parserState.isLastCharEscapeChar() && (c == SINGLE_QUOTE_CHAR) && !parserState.isDoubleQuoteType()) {
            parserState.setInQuotes(false);
        } else if (!parserState.isLastCharEscapeChar() && (c == DOUBLE_QUOTE_CHAR) && parserState.isDoubleQuoteType()) {
            parserState.setInQuotes(false);
        } else if (c == SPACE_CHAR) {
            newString.append(ESCAPE_SEQUENCE_FOR_SPACE_CHAR);
        } else if (c == TAB_CHAR) {
            newString.append(ESCAPE_SEQUENCE_FOR_TAB_CHAR);
        } else {
            newString.append(c);
        }
    }

    private void processNonQuotedChar(final ParserState parserState, final char c, final StringBuilder newString) {
        if (!parserState.isLastCharEscapeChar() && (c == SINGLE_QUOTE_CHAR)) {
            parserState.setInQuotes(true);
            parserState.setQuoteTypeIsDouble(false);
        } else if (!parserState.isLastCharEscapeChar() && (c == DOUBLE_QUOTE_CHAR)) {
            parserState.setInQuotes(true);
            parserState.setQuoteTypeIsDouble(true);
        } else {
            newString.append(c);
        }
    }

    private class ParserState {
        private boolean lastCharWasEscapeChar = false;
        private boolean inQuotes = false;
        private boolean quoteTypeIsDouble = false;

        public boolean isLastCharEscapeChar() {
            return lastCharWasEscapeChar;
        }

        public boolean isInQuotes() {
            return inQuotes;
        }

        public boolean isDoubleQuoteType() {
            return quoteTypeIsDouble;
        }

        public void setLastCharWasEscapeChar(final boolean lastCharWasEscapeChar) {
            this.lastCharWasEscapeChar = lastCharWasEscapeChar;
        }

        public void setInQuotes(final boolean inQuotes) {
            this.inQuotes = inQuotes;
        }

        public void setQuoteTypeIsDouble(final boolean quoteTypeIsDouble) {
            this.quoteTypeIsDouble = quoteTypeIsDouble;
        }
    }
}
