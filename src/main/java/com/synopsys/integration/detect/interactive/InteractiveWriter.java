package com.synopsys.integration.detect.interactive;

import java.io.Console;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.interactive.reader.ConsoleInteractiveReader;
import com.synopsys.integration.detect.interactive.reader.InteractiveReader;
import com.synopsys.integration.detect.interactive.reader.ScannerInteractiveReader;

public class InteractiveWriter {
    private final PrintStream printStream;
    private final InteractiveReader interactiveReader;

    public static InteractiveWriter defaultWriter(Console console, InputStream systemIn, PrintStream sysOut) {
        Logger staticLogger = LoggerFactory.getLogger(InteractiveWriter.class);

        // Using an UncloseablePrintStream so we don't accidentally close System.out
        PrintStream interactivePrintStream = new UncloseablePrintStream(sysOut);
        InteractiveReader interactiveReader;

        if (console != null) {
            interactiveReader = new ConsoleInteractiveReader(console);
        } else {
            staticLogger.warn("It may be insecure to enter passwords because you are running in a virtual console.");
            interactiveReader = new ScannerInteractiveReader(systemIn);
        }

        return new InteractiveWriter(interactivePrintStream, interactiveReader);
    }

    public InteractiveWriter(PrintStream printStream, InteractiveReader interactiveReader) {
        this.printStream = printStream;
        this.interactiveReader = interactiveReader;
    }

    public void promptToStartDetect() {
        printStream.println();
        printStream.println("Ready to start Detect. Hit enter to proceed.");
        interactiveReader.readLine();
    }

    public String askQuestion(String question) {
        printStream.println(question);
        return interactiveReader.readLine();
    }

    public String askSecretQuestion(String question) {
        printStream.println(question);
        return interactiveReader.readPassword();
    }

    public Boolean askYesOrNo(String question) {
        return askYesOrNoWithMessage(question, null);
    }

    public void println(String x) {
        printStream.println(x);
    }

    public void println() {
        printStream.println();
    }

    public void println(Exception e) {
        printStream.println(e);
    }

    public Boolean askYesOrNoWithMessage(String question, String message) {
        printStream.print(question);
        if (StringUtils.isNotBlank(message)) {
            printStream.print(" " + message);
        }
        printStream.print(" (Y|n)");
        printStream.println();
        final int maxAttempts = 3;
        int attempts = 0;
        while (attempts < maxAttempts) {
            String response = interactiveReader.readLine();
            if (anyEquals(response, "y", "yes")) {
                return true;
            } else if (anyEquals(response, "n", "no")) {
                return false;
            }
            attempts += 1;
            printStream.println("Please answer yes or no.");
        }
        return false;
    }

    private boolean anyEquals(String response, String... options) {
        String trimmed = response.trim().toLowerCase();
        for (String opt : options) {
            if (trimmed.equals(opt)) {
                return true;
            }
        }
        return false;
    }
}
