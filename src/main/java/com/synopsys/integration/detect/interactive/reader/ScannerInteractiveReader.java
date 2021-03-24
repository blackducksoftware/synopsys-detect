/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.interactive.reader;

import java.io.InputStream;
import java.util.Scanner;

public class ScannerInteractiveReader implements InteractiveReader {
    private final Scanner scanner;

    public ScannerInteractiveReader(final Scanner scanner) {
        this.scanner = scanner;
    }

    public ScannerInteractiveReader(final InputStream stream) {
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
