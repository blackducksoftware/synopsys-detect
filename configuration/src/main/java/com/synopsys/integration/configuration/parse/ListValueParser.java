package com.synopsys.integration.configuration.parse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class ListValueParser<T> extends ValueParser<List<T>> {
    @NotNull
    private final ValueParser<T> valueParser;

    @NotNull
    private final String delimiter;

    public ListValueParser(@NotNull ValueParser<T> valueParser) {
        this(valueParser, ",");
    }

    public ListValueParser(@NotNull ValueParser<T> valueParser, @NotNull String delimiter) {
        this.valueParser = valueParser;
        this.delimiter = delimiter;
    }

    @NotNull
    @Override
    public List<T> parse(@NotNull String rawValue) throws ValueParseException {
        List<T> parsedValues = new ArrayList<>();

        for (String element : StringUtils.splitPreserveAllTokens(rawValue, delimiter)) {
            String trimmedElement = element.trim();

            if (StringUtils.isBlank(trimmedElement)) {
                throw new ValueParseException(rawValue, "List",
                    String.format(
                        "Failed to parse list '%s'. The list must be comma separated and each element in the list must not be empty (at least one character that is not whitespace).",
                        rawValue
                    )
                );
            } else {
                parsedValues.add(valueParser.parse(trimmedElement));
            }
        }

        return parsedValues;
    }
}
