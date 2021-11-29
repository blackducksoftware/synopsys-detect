/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pear;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.PearResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

import java.io.File;

@DetectableInfo(language = "PHP", forge = "Pear", requirementsMarkdown = "Files: package.xml. Executable: pear.")
public class PearCliDetectable extends Detectable {
    public static final String PACKAGE_XML_FILENAME = "package.xml";

    private final FileFinder fileFinder;
    private final PearResolver pearResolver;
    private final PearCliExtractor pearCliExtractor;
    private final PearCliDetectableOptions pearCliDetectableOptions;

    private ExecutableTarget pearExe;
    private File packageDotXml;

    public PearCliDetectable(DetectableEnvironment environment, FileFinder fileFinder, PearResolver pearResolver, PearCliExtractor pearCliExtractor, PearCliDetectableOptions pearCliDetectableOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.pearResolver = pearResolver;
        this.pearCliExtractor = pearCliExtractor;
        this.pearCliDetectableOptions = pearCliDetectableOptions;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        packageDotXml = requirements.file(PACKAGE_XML_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        pearExe = requirements.executable(pearResolver::resolvePear, "pear");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return pearCliExtractor.extract(pearExe, packageDotXml, environment.getDirectory(), pearCliDetectableOptions.onlyGatherRequired());
    }

}
