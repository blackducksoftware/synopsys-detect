/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.packman.packagemanager.rubygems;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.google.gson.GsonBuilder;

public class SimpleParserTest {
    private final String indentation = "--";

    private final String objectIdentifier = "->";

    private SimpleParser simpleParser;

    private SimpleParser gemlockParser;

    @Before
    public void init() {
        simpleParser = new SimpleParser(indentation, objectIdentifier);
        gemlockParser = new SimpleParser("  ", ":");
    }

    @Test
    public void indentationTest() {
        final int level = 3;
        String indentation = "";
        for (int i = 0; i < level; i++) {
            indentation += this.indentation;
        }
        indentation += "Test Text";
        final int currentLevel = simpleParser.getCurrentLevel(indentation);
        assertEquals(level, currentLevel);
    }

    @Test
    public void lineToEntryTest() {
        final String key = "myObjectName";
        final Entry<String, ParserMap> entry = simpleParser.lineToEntry(key);
        assertTrue(entry.getKey().equals(key));
        assertNotNull(entry.getValue());
        assertTrue(entry.getValue().isEmpty());
    }

    @Test
    public void lineToEntryWithObjectIdentifierTest() {
        final String key = "myObjectName";
        final Entry<String, ParserMap> entry = simpleParser.lineToEntry(key + objectIdentifier);
        assertTrue(entry.getKey().equals(key));
        assertNotNull(entry.getValue());
        assertTrue(entry.getValue().isEmpty());
    }

    @Test
    public void lineToEntryWithObjectIdentifierValueTest() {
        final String key = "myObjectName";
        final String value = "My value is this";
        final Entry<String, ParserMap> entry = simpleParser.lineToEntry(String.format("%s%s %s", key, objectIdentifier, value));
        assertTrue(entry.getKey().equals(key));
        assertNotNull(entry.getValue());
        assertTrue(entry.getValue().containsKey(value));
    }

    @Test
    public void parseTest() throws IOException, JSONException {
        final String gemlockText = IOUtils.toString(getClass().getResourceAsStream("/rubygems/Gemfile.lock"), StandardCharsets.UTF_8);
        final ParserMap parserMap = gemlockParser.parse(gemlockText);
        final String actual = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(parserMap);
        final String expected = IOUtils.toString(getClass().getResourceAsStream("/rubygems/expected.json"), StandardCharsets.UTF_8);
        JSONAssert.assertEquals(expected, actual, false);
    }
}
