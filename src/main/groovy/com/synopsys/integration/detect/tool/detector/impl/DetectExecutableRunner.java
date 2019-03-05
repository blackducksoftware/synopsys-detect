package com.synopsys.integration.detect.tool.detector.impl;

import java.io.InputStream;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.Executable;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectable.executable.impl.ExecutableStreamThread;

public class DetectExecutableRunner implements ExecutableRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ExecutableOutput execute(final Executable executable) throws ExecutableRunnerException {
        Consumer<String> standardLoggingMethod = str -> logger.info(str);//TODO: should this go back to silent things?
        Consumer<String> traceLoggingMethod = str -> logger.trace(str);
        standardLoggingMethod.accept(String.format("Running executable >%s", executable.getMaskedExecutableDescription()));
        try {
            final ProcessBuilder processBuilder = executable.createProcessBuilder();
            final Process process = processBuilder.start();

            try (InputStream standardOutputStream = process.getInputStream(); InputStream standardErrorStream = process.getErrorStream()) {
                final ExecutableStreamThread standardOutputThread = new ExecutableStreamThread(standardOutputStream, standardLoggingMethod, traceLoggingMethod);
                standardOutputThread.start();

                final ExecutableStreamThread errorOutputThread = new ExecutableStreamThread(standardErrorStream, standardLoggingMethod, traceLoggingMethod);
                errorOutputThread.start();

                final int returnCode = process.waitFor();
                standardLoggingMethod.accept("Executable finished: " + returnCode);

                standardOutputThread.join();
                errorOutputThread.join();

                final String standardOutput = standardOutputThread.getExecutableOutput().trim();
                final String errorOutput = errorOutputThread.getExecutableOutput().trim();

                final ExecutableOutput output = new ExecutableOutput(returnCode, standardOutput, errorOutput);
                return output;
            }
        } catch (final Exception e) {
            throw new ExecutableRunnerException(e);
        }
    }
}
