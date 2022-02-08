package com.synopsys.integration.configuration.parse;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.Bds;

class ListValueParserTest {
    private static class TestValueParser extends ValueParser<String> {
        @NotNull
        @Override
        public String parse(@NotNull String value) throws ValueParseException {
            if ("-1".equals(value)) {
                throw new ValueParseException(value, "String", "Can convert anything but this value to a String.");
            }
            return value;
        }
    }

    private static class TestDefaultListValueParser extends ListValueParser<String> {
        final ValueParser<String> valueParser;

        public TestDefaultListValueParser(ValueParser<String> valueParser) {
            super(valueParser);
            this.valueParser = valueParser;
        }
    }

    private static class TestCustomListValueParser extends ListValueParser<String> {
        final ValueParser<String> valueParser;

        public TestCustomListValueParser(ValueParser<String> valueParser, String delimiter) {
            super(valueParser, delimiter);
            this.valueParser = valueParser;
        }
    }

    @Test
    public void parseDefault() throws ValueParseException {
        ListValueParser<String> listValueParser = new TestDefaultListValueParser(new TestValueParser());
        List<String> actualValues = listValueParser.parse("test,this,example , parser");
        Assertions.assertEquals(Bds.listOf("test", "this", "example", "parser"), actualValues, "The list parser should be splitting on comma and trimming by default.");
    }

    @Test
    public void parseCustomDelimiters() throws ValueParseException {
        ListValueParser<String> listValueParser = new TestCustomListValueParser(new TestValueParser(), "|");
        List<String> actualValues = listValueParser.parse("test this|parser|for real");
        Assertions.assertEquals(Bds.listOf("test this", "parser", "for real"), actualValues);
    }

    @Test
    public void failsToParseInvalidElement() {
        ListValueParser<String> listValueParser = new TestDefaultListValueParser(new TestValueParser());
        Assertions.assertThrows(ValueParseException.class, () -> listValueParser.parse("test,should,throw,-1,for,test"));
    }

    @Test
    public void failsToParseEmpty() {
        ListValueParser<String> listValueParser = new TestDefaultListValueParser(new TestValueParser());
        Assertions.assertThrows(ValueParseException.class, () -> listValueParser.parse("should,,throw"));
    }

    @Test
    public void failsToParseWhitespace() {
        ListValueParser<String> listValueParser = new TestDefaultListValueParser(new TestValueParser());
        Assertions.assertThrows(ValueParseException.class, () -> listValueParser.parse("should,  ,throw"));
    }
}