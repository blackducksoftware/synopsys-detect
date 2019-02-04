package com.synopsys.integration.detectable.detectable.executable.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class ExecutableStreamThread extends Thread {
    private final BufferedReader bufferedReader;
    private final StringBuilder stringBuilder;
    private final Consumer<String> outputLoggingMethod;
    private final Consumer<String> traceLoggingMethod;

    private String executableOutput;

    public ExecutableStreamThread(final InputStream executableStream, final Consumer<String> outputLoggingMethod, final Consumer<String> traceLoggingMethod) {
        super(Thread.currentThread().getName() + "-Executable_Stream_Thread");
        this.outputLoggingMethod = outputLoggingMethod;
        this.traceLoggingMethod = traceLoggingMethod;
        final InputStreamReader reader = new InputStreamReader(executableStream, StandardCharsets.UTF_8);
        this.bufferedReader = new BufferedReader(reader);
        this.stringBuilder = new StringBuilder();
    }

    @Override
    public void run() {
        try {
            String line;
            final String separator = System.lineSeparator();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + separator);
                outputLoggingMethod.accept(line);
            }
        } catch (final IOException e) {
            // Ignore
            traceLoggingMethod.accept(e.toString());
        }
        this.executableOutput = stringBuilder.toString();
    }

    public String getExecutableOutput() {
        return executableOutput;
    }

}
