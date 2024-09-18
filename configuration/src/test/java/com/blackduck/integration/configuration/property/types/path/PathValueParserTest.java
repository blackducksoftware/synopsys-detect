package com.blackduck.integration.configuration.property.types.path;

import com.blackduck.integration.configuration.parse.ValueParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PathValueParserTest {
    @ParameterizedTest
    @ValueSource(strings = { "", " ", "     " })
    public void parseEmpty(String value) {
        Assertions.assertThrows(ValueParseException.class, () -> new PathValueParser().parse(value));
    }

    @Test
    public void parseValid() throws ValueParseException {
        Assertions.assertEquals(new PathValue("/valid/path"), new PathValueParser().parse("/valid/path"));
    }
}