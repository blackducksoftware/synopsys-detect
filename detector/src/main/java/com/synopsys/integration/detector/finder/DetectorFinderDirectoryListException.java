/**
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.finder;

import java.io.IOException;

public class DetectorFinderDirectoryListException extends Exception {
    public DetectorFinderDirectoryListException(final String message, final IOException e) {
        super(message, e);
    }
}
