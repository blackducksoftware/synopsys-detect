/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.xcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.xcode.model.PackageResolved;
import com.synopsys.integration.detectable.detectables.xcode.process.PackageResolvedFormatChecker;
import com.synopsys.integration.detectable.detectables.xcode.process.PackageResolvedTransformer;
import com.synopsys.integration.detectable.extraction.Extraction;

public class XcodeSwiftExtractor {
    private final Gson gson;
    private final PackageResolvedFormatChecker packageResolvedFormatChecker;
    private final PackageResolvedTransformer packageResolvedTransformer;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public XcodeSwiftExtractor(Gson gson, PackageResolvedFormatChecker packageResolvedFormatChecker, PackageResolvedTransformer packageResolvedTransformer) {
        this.gson = gson;
        this.packageResolvedFormatChecker = packageResolvedFormatChecker;
        this.packageResolvedTransformer = packageResolvedTransformer;
    }

    public Extraction extract(File foundPackageResolvedFile, File foundXcodeProjectFile) throws FileNotFoundException {
        FileReader fileReader = new FileReader(foundPackageResolvedFile);
        PackageResolved packageResolved = gson.fromJson(fileReader, PackageResolved.class);

        if (packageResolved == null) {
            // There are no dependencies to extract.
            DependencyGraph dependencyGraph = new MutableMapDependencyGraph();
            CodeLocation emptyCodeLocation = new CodeLocation(dependencyGraph, foundXcodeProjectFile);
            return new Extraction.Builder().success(emptyCodeLocation).build();
        }

        packageResolvedFormatChecker.handleVersionCompatibility(
            packageResolved,
            (fileFormatVersion, knownVersions) -> logger.warn(String.format("The format version of Package.resolved (%s) is unknown to Detect, but will attempt to parse anyway. Known format versions are (%s).",
                fileFormatVersion,
                StringUtils.join(knownVersions, ", ")
            ))
        );

        DependencyGraph dependencyGraph = packageResolvedTransformer.transform(packageResolved);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph);

        return new Extraction.Builder()
            .success(codeLocation)
            .build();
    }
}
