package com.synopsys.integration.detect.help;

import org.junit.Assert;
import org.junit.Test;

public class ArgumentStateParserTests {
    private final DetectArgumentStateParser parser = new DetectArgumentStateParser();

    @Test
    public void helpParsesValue() {

        final String[] args = new String[] { "-h", "value" };
        final DetectArgumentState state = parser.parseArgs(args);

        Assert.assertTrue(state.isHelp());
        Assert.assertEquals("value", state.getParsedValue());
    }

    @Test
    public void helpIgnoresDash() {

        final String[] args = new String[] { "-h", "-ignoreme" };
        final DetectArgumentState state = parser.parseArgs(args);

        Assert.assertTrue(state.isHelp());
        Assert.assertNull(state.getParsedValue());
    }

    @Test
    public void helpParsesInMiddleWithNoValue() {

        final String[] args = new String[] { "--propert", "--property", "-h", "--property", "--property" };
        final DetectArgumentState state = parser.parseArgs(args);

        Assert.assertTrue(state.isHelp());
        Assert.assertNull(state.getParsedValue());
    }

    @Test
    public void helpParsesEndValue() {

        final String[] args = new String[] { "--property", "--property", "-h", "value" };
        final DetectArgumentState state = parser.parseArgs(args);

        Assert.assertTrue(state.isHelp());
        Assert.assertEquals("value", state.getParsedValue());
    }

    @Test
    public void helpParsesStartValue() {

        final String[] args = new String[] { "-h", "value", "--property", "--property", "--property" };
        final DetectArgumentState state = parser.parseArgs(args);

        Assert.assertTrue(state.isHelp());
        Assert.assertEquals("value", state.getParsedValue());
    }

}
