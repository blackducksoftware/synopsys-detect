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

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.xcode.model.PackageResolved;
import com.synopsys.integration.detectable.extraction.Extraction;

public class XcodeSwiftExtractor {
    private final Gson gson;
    private final PackageResolvedTransformer packageResolvedTransformer;

    public XcodeSwiftExtractor(Gson gson, PackageResolvedTransformer packageResolvedTransformer) {
        this.gson = gson;
        this.packageResolvedTransformer = packageResolvedTransformer;
    }

    public Extraction extract(File foundPackageResolvedFile) {
        try {
            FileReader fileReader = new FileReader(foundPackageResolvedFile);
            PackageResolved packageResolved = gson.fromJson(fileReader, PackageResolved.class);
            DependencyGraph dependencyGraph = packageResolvedTransformer.transform(packageResolved);
            CodeLocation codeLocation = new CodeLocation(dependencyGraph);

            return new Extraction.Builder()
                .success(codeLocation)
                .build();
        } catch (FileNotFoundException exception) {
            return new Extraction.Builder()
                .exception(exception)
                .build();
        }
    }
}
