package com.synopsys.integration.detect.interactive.reader;

import java.io.InputStream;
import java.util.Scanner;

public class ScannerInteractiveReader implements InteractiveReader {
    private final Scanner scanner;

    public ScannerInteractiveReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public ScannerInteractiveReader(InputStream stream) {
        this.scanner = new Scanner(stream);
    }

    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    @Override
    public String readPassword() {
        return scanner.nextLine();
    }

}
