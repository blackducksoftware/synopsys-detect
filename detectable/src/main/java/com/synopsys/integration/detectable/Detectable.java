/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

public abstract class Detectable {
    protected DetectableEnvironment environment;

    public Detectable(final DetectableEnvironment environment) {
        this.environment = environment;
    }

    /*
     * Applicable should be light-weight and should never throw an exception. Look for files, check properties, short and sweet.
     */
    public abstract DetectableResult applicable();

    /*
     * Extractable may be as heavy as needed, and may (and sometimes should) fail. Make web requests, install inspectors or run executables.
     */
    public abstract DetectableResult extractable() throws DetectableException;

    /*
     * Perform project information discovery and try not to throw an exception. Instead return a discovery built with an exception.
     */
    public Discovery discover(final ExtractionEnvironment extractionEnvironment) {
        try {
            final Extraction extraction = extract(extractionEnvironment);
            if (extraction.isSuccess()) {
                return new Discovery.Builder().success(extraction).build();
            } else {
                return new Discovery.Builder().failure("The extraction was not a success.").build();
            }
        } catch (ExecutableFailedException e) {
            return new Discovery.Builder().failure("The extraction was not a success. An executable returned a non-zero exit code.").build();
        }

    }

    /*
     * Perform the extraction and try not to throw an exception. Instead return an extraction built with an exception.
     */
    public abstract Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException;
}
