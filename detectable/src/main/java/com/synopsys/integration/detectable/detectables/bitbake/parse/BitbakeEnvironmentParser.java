package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeEnvironment;

public class BitbakeEnvironmentParser {
    private static final String ARCHITECTURE_VARIABLE_NAME = "MACHINE_ARCH";
    private static final String LICENSESDIR_VARIABLE_NAME = "LICENSE_DIRECTORY";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Predicate<String> isArchitectureLine =  l -> l.startsWith(ARCHITECTURE_VARIABLE_NAME+"=");
    private final Predicate<String> isLicensesDirLine =  l -> l.startsWith(LICENSESDIR_VARIABLE_NAME+"=");

    public BitbakeEnvironment parseArchitecture(List<String> bitbakeEnvironmentCmdOutput) {
        Optional<String> architecture = bitbakeEnvironmentCmdOutput.stream()
            .filter(isArchitectureLine)
            .map((line) -> isolateVariableValue(line, ARCHITECTURE_VARIABLE_NAME))
            .map(this::unquote)
            .findFirst();

        Optional<String> licensesDirPath = bitbakeEnvironmentCmdOutput.stream()
            .filter(isLicensesDirLine)
            .map((line) -> isolateVariableValue(line, LICENSESDIR_VARIABLE_NAME))
            .map(this::unquote)
            .findFirst();

        BitbakeEnvironment bitbakeEnvironment = new BitbakeEnvironment(architecture.orElse(null), licensesDirPath.orElse(null));
        logger.debug("Bitbake environment: {}", bitbakeEnvironment);
        return bitbakeEnvironment;
    }

    private String isolateVariableValue(String line, String variableName) {
        return line.substring(variableName.length()+1);
    }

    private String unquote(String s) {
        if ( (s.startsWith("\"") && s.endsWith("\"")) ||
            s.startsWith("'") && s.endsWith("'") ) {
            return s.substring(1, s.length()-1);
        }
        return s;
    }
}
