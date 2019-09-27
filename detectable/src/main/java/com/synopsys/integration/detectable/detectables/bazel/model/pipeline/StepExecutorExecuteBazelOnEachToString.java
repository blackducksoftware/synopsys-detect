package com.synopsys.integration.detectable.detectables.bazel.model.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.naming.OperationNotSupportedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.bazel.parse.BazelVariableSubstitutor;
import com.synopsys.integration.exception.IntegrationException;

public class StepExecutorExecuteBazelOnEachToString implements StepExecutor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String FAKE_OUTPUT = "results {\n"
                                           + "  target {\n"
                                           + "    type: RULE\n"
                                           + "    rule {\n"
                                           + "      name: \"@maven//:androidx_test_espresso_espresso_core\"\n"
                                           + "      attribute {\n"
                                           + "        name: \"tags\"\n"
                                           + "        type: STRING_LIST\n"
                                           + "        string_list_value: \"maven_coordinates=androidx.test.espresso:espresso-core:3.1.1\"\n"
                                           + "        explicitly_specified: true\n"
                                           + "        nodep: false\n"
                                           + "      }\n"
                                           + "    }\n"
                                           + "  }\n"
                                           + "  configuration {\n"
                                           + "    checksum: \"a97574deb0f0815b04aefb212765a5ae\"\n"
                                           + "  }\n"
                                           + "}\n"
        + "results {\n"
            + "  target {\n"
            + "    type: RULE\n"
            + "    rule {\n"
            + "      name: \"@maven//:junit_junit\"\n"
            + "      attribute {\n"
            + "        name: \"tags\"\n"
            + "        type: STRING_LIST\n"
            + "        string_list_value: \"maven_coordinates=junit:junit:4.12\"\n"
            + "        explicitly_specified: true\n"
            + "        nodep: false\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "  configuration {\n"
            + "    checksum: \"a97574deb0f0815b04aefb212765a5ae\"\n"
            + "  }\n"
            + "}\n";


    private final BazelCommandExecutor bazelCommandExecutor;
    private final BazelVariableSubstitutor bazelVariableSubstitutor;

    public StepExecutorExecuteBazelOnEachToString(final BazelCommandExecutor bazelCommandExecutor, final BazelVariableSubstitutor bazelVariableSubstitutor) {
        this.bazelCommandExecutor = bazelCommandExecutor;
        this.bazelVariableSubstitutor = bazelVariableSubstitutor;
    }

    @Override
    public boolean applies(final String stepType) {
        if ("executeBazelOnEach".equalsIgnoreCase(stepType)) {
            logger.info("StepExecutorExecuteBazelOnEachToString applies");
            return true;
        }
        return false;
    }

    @Override
    public List<String> process(final Step step, final List<String> input) throws IntegrationException {
        final List<String> finalizedArgs;
        if (input.size() == 0) {
            finalizedArgs = bazelVariableSubstitutor.substitute(step.getArgs(), null);
            final Optional<String> cmdOutput = bazelCommandExecutor.executeToString(finalizedArgs);
            final List<String> results = new ArrayList<>(1);
            if (cmdOutput.isPresent()) {
                results.add(cmdOutput.get());
            }
            return results;
        }
        throw new UnsupportedOperationException("Have not implemented this yet");
    }
}
