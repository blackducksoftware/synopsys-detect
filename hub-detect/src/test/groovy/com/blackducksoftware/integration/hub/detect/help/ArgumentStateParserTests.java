package com.blackducksoftware.integration.hub.detect.help;

import org.junit.Assert;
import org.junit.Test;

public class ArgumentStateParserTests {

    ArgumentStateParser parser = new ArgumentStateParser();

    @Test
    public void helpParsesValue() {

        final String[] args = new String[] {"-h", "value"};
        final ArgumentState state = parser.parseArgs(args);

        Assert.assertTrue(state.isHelp);
        Assert.assertEquals(state.parsedValue, "value");
    }

    @Test
    public void helpIgnoresDash() {

        final String[] args = new String[] {"-h", "-ignoreme"};
        final ArgumentState state = parser.parseArgs(args);

        Assert.assertTrue(state.isHelp);
        Assert.assertNull(state.parsedValue);
    }

    @Test
    public void helpParsesInMiddleWithNoValue() {

        final String[] args = new String[] {"--propert", "--property", "-h", "--property", "--property"};
        final ArgumentState state = parser.parseArgs(args);

        Assert.assertTrue(state.isHelp);
        Assert.assertNull(state.parsedValue);
    }

    @Test
    public void helpParsesEndValue() {

        final String[] args = new String[] {"--property", "--property", "-h", "value"};
        final ArgumentState state = parser.parseArgs(args);

        Assert.assertTrue(state.isHelp);
        Assert.assertEquals(state.parsedValue, "value");
    }

    @Test
    public void helpParsesStartValue() {

        final String[] args = new String[] {"-h", "value", "--property", "--property", "--property"};
        final ArgumentState state = parser.parseArgs(args);

        Assert.assertTrue(state.isHelp);
        Assert.assertEquals(state.parsedValue, "value");
    }



}
