package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.List;
import java.util.Optional;

public class BitbakeEnvironmentParser {
    private static final String ARCHITECTURE_VARIABLE_NAME = "MACHINE_ARCH";

    public Optional<String> parseArchitecture(List<String> bitbakeEnvironmentCmdOutput) {
        Optional<String> machineArchLine = bitbakeEnvironmentCmdOutput.stream()
            .filter(l -> l.startsWith(ARCHITECTURE_VARIABLE_NAME+"="))
            .map(this::isolateArchitectureValue)
            .map(this::unquote)
            .findFirst();

        return machineArchLine;
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
