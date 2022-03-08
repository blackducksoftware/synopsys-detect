package com.synopsys.integration.detectable.detectables.clang.compilecommand;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.parse.CommandParser;

public class CompileCommandParser {
    private final CommandParser commandParser;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CompileCommandParser(CommandParser commandParser) {
        this.commandParser = commandParser;
    }

    public List<String> parseCommand(CompileCommand compileCommand, Map<String, String> optionOverrides) {
        String commandString = compileCommand.getCommand();
        if (StringUtils.isBlank(commandString)) {
            commandString = String.join(" ", compileCommand.getArguments());
        }
        List<String> options = commandParser.parseCommandString(commandString);

        try {
            performOverrides(options, optionOverrides);
        } catch (OverrideOptionWithNoValueException e) {
            logger.debug(String.format("Failed option override in command %s:  %s", commandString, e.getMessage()));
        }

        return options;
    }

    private static void performOverrides(List<String> options, Map<String, String> optionOverrides) throws OverrideOptionWithNoValueException {
        for (Map.Entry<String, String> override : optionOverrides.entrySet()) {
            String optionToOverride = override.getKey();
            if (options.contains(optionToOverride)) {
                int indexOfOptionToOverride = options.indexOf(optionToOverride);
                if (indexOfOptionToOverride < options.size() - 1) {
                    options.set(indexOfOptionToOverride + 1, override.getValue());
                } else {
                    throw new OverrideOptionWithNoValueException(String.format("Option %s could not be overrode since it does not have a value", optionToOverride));
                }
            }
        }
    }
}
