package com.synopsys.integration.detect.interactive.reader;

import java.io.Console;

public class ConsoleInteractiveReader implements InteractiveReader {
    private final Console console;

    public ConsoleInteractiveReader(Console console) {
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
