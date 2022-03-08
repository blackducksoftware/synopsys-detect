package com.synopsys.integration.detect.interactive;

import java.io.PrintStream;

/**
 * A wrapped PrintStream that does nothing when closed.
 * Useful for wrapping System.out without the underlying stream being closed.
 */
public class UncloseablePrintStream extends PrintStream {
    public UncloseablePrintStream(PrintStream printStream) {
        super(printStream);
    }

    @Override
    public void close() {
        // Do nothing
    }
}
