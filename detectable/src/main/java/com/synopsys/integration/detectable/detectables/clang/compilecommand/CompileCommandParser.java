package com.synopsys.integration.detectable.detectables.clang.compilecommand;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.common.util.parse.CommandParser;

public class CompileCommandParser {
    private final CommandParser commandParser;

    public CompileCommandParser(CommandParser commandParser) {
        this.commandParser = commandParser;
    }

    public List<String> parseCommand(CompileCommand compileCommand, Map<String, String> optionOverrides) {

        String commandString = compileCommand.command;
        if (StringUtils.isBlank(commandString)) {
            commandString = String.join(" ", compileCommand.arguments);
        }
        return commandParser.parseCommandString(commandString, optionOverrides);
    }
}
