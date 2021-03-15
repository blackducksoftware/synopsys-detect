/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.vendr;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Golang", forge = "GitHub", requirementsMarkdown = "File: vendor.conf.")
public class GoVndrDetectable extends Detectable {
    public static final String VNDR_CONF_FILENAME = "vendor.conf";

    private final FileFinder fileFinder;
    private final GoVndrExtractor goVndrExtractor;

    private File vndrConfig;

    public GoVndrDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final GoVndrExtractor goVndrExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.goVndrExtractor = goVndrExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        vndrConfig = requirements.file(VNDR_CONF_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return goVndrExtractor.extract(vndrConfig);
    }

}
