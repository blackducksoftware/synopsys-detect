package com.synopsys.integration.detect.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.configuration.help.DetectArgumentState;
import com.synopsys.integration.detect.configuration.help.DetectArgumentStateParser;

public class ArgumentStateParserTests {
    private final DetectArgumentStateParser parser = new DetectArgumentStateParser();

    @Test
    public void helpParsesValue() {
        String[] args = new String[] { "-h", "value" };
        DetectArgumentState state = parser.parseArgs(args);

        Assertions.assertTrue(state.isHelp());
        Assertions.assertEquals("value", state.getParsedValue());
    }

    @Test
    public void helpIgnoresDash() {
        String[] args = new String[] { "-h", "-ignoreme" };
        DetectArgumentState state = parser.parseArgs(args);

        Assertions.assertTrue(state.isHelp());
        Assertions.assertNull(state.getParsedValue());
    }

    @Test
    public void helpParsesInMiddleWithNoValue() {
        String[] args = new String[] { "--propert", "--property", "-h", "--property", "--property" };
        DetectArgumentState state = parser.parseArgs(args);

        Assertions.assertTrue(state.isHelp());
        Assertions.assertNull(state.getParsedValue());
    }

    @Test
    public void helpParsesEndValue() {
        String[] args = new String[] { "--property", "--property", "-h", "value" };
        DetectArgumentState state = parser.parseArgs(args);

        Assertions.assertTrue(state.isHelp());
        Assertions.assertEquals("value", state.getParsedValue());
    }

    @Test
    public void helpParsesStartValue() {
        String[] args = new String[] { "-h", "value", "--property", "--property", "--property" };
        DetectArgumentState state = parser.parseArgs(args);

        Assertions.assertTrue(state.isHelp());
        Assertions.assertEquals("value", state.getParsedValue());
    }

}
