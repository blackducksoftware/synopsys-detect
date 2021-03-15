/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.interactive;

import java.io.PrintStream;

/**
 * A wrapped PrintStream that does nothing when closed.
 * Useful for wrapping System.out without the underlying stream being closed.
 */
public class UncloseablePrintStream extends PrintStream {
    public UncloseablePrintStream(final PrintStream printStream) {
        super(printStream);
    }

    @Override
    public void close() {
        // Do nothing
    }
}
