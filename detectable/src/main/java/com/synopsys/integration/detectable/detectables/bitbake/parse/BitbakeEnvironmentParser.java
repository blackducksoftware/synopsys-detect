package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class BitbakeEnvironmentParser {
    private static final String ARCHITECTURE_VARIABLE_NAME = "MACHINE_ARCH";
    private final Predicate<String> isArchitectureLine =  l -> l.startsWith(ARCHITECTURE_VARIABLE_NAME+"=");

    public Optional<String> parseArchitecture(List<String> bitbakeEnvironmentCmdOutput) {
        return bitbakeEnvironmentCmdOutput.stream()
            .filter(isArchitectureLine)
            .map(this::isolateArchitectureValue)
            .map(this::unquote)
            .findFirst();
    }

    private String isolateArchitectureValue(String s) {
        return s.substring(ARCHITECTURE_VARIABLE_NAME.length()+1);
    }

    private String unquote(String s) {
        if ( (s.startsWith("\"") && s.endsWith("\"")) ||
            s.startsWith("'") && s.endsWith("'") ) {
            return s.substring(1, s.length()-1);
        }
        return s;
    }
}
