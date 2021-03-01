/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.interactive.reader;

import java.io.Console;

public class ConsoleInteractiveReader implements InteractiveReader {
    private final Console console;

    public ConsoleInteractiveReader(final Console console) {
        this.console = console;
    }

    @Override
    public String readLine() {
        return console.readLine();
    }

    @Override
    public String readPassword() {
        return new String(console.readPassword());
    }

}
