package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.bitbake.data.BitbakeEnvironment;

public class BitbakeEnvironmentParser {
    private static final String ARCHITECTURE_VARIABLE_NAME = "MACHINE_ARCH";
    private static final String LICENSES_DIR_VARIABLE_NAME = "LICENSE_DIRECTORY";
    private static final String MACHINE_VARIABLE_NAME      = "MACHINE";
    private static final String QUOTE_CHARS = "\"'";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Predicate<String> isArchitectureLine = l -> l.startsWith(ARCHITECTURE_VARIABLE_NAME + "=");
    private final Predicate<String> isLicensesDirLine = l -> l.startsWith(LICENSES_DIR_VARIABLE_NAME + "=");
    private final Predicate<String> isMachineLine = l -> l.startsWith(MACHINE_VARIABLE_NAME + "=");

    public BitbakeEnvironment parse(List<String> bitbakeEnvironmentCmdOutput) {
        Optional<String> architecture = bitbakeEnvironmentCmdOutput.stream()
            .filter(isArchitectureLine)
            .map(line -> isolateVariableValue(line, ARCHITECTURE_VARIABLE_NAME))
            .map(s -> StringUtils.strip(s, QUOTE_CHARS))
            .findFirst();

        Optional<String> licensesDirPath = bitbakeEnvironmentCmdOutput.stream()
            .filter(isLicensesDirLine)
            .map(line -> isolateVariableValue(line, LICENSES_DIR_VARIABLE_NAME))
            .map(s -> StringUtils.strip(s, QUOTE_CHARS))
            .findFirst();

        Optional<String> machine = bitbakeEnvironmentCmdOutput.stream()
            .filter(isMachineLine)
            .map(line -> isolateVariableValue(line, MACHINE_VARIABLE_NAME))
            .map(s -> StringUtils.strip(s, QUOTE_CHARS))
            .findFirst();

        BitbakeEnvironment bitbakeEnvironment = new BitbakeEnvironment(
            architecture.orElse(null),
            licensesDirPath.orElse(null),
            machine.orElse(null)
        );
        logger.debug("Bitbake environment: {}", bitbakeEnvironment);
        return bitbakeEnvironment;
    }

    private String isolateVariableValue(String line, String variableName) {
        return line.substring(variableName.length() + 1);
    }
}
