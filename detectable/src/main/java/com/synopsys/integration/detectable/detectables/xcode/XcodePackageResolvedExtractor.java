package com.synopsys.integration.detectable.detectables.xcode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.xcode.data.PackageResolved;
import com.synopsys.integration.detectable.detectables.xcode.parse.PackageResolvedFormatChecker;
import com.synopsys.integration.detectable.detectables.xcode.parse.PackageResolvedParser;
import com.synopsys.integration.detectable.detectables.xcode.transform.PackageResolvedTransformer;
import com.synopsys.integration.detectable.extraction.Extraction;

public class XcodePackageResolvedExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PackageResolvedParser packageResolvedParser;
    private final PackageResolvedFormatChecker packageResolvedFormatChecker;
    private final PackageResolvedTransformer packageResolvedTransformer;

    public XcodePackageResolvedExtractor(
        PackageResolvedParser packageResolvedParser,
        PackageResolvedFormatChecker packageResolvedFormatChecker,
        PackageResolvedTransformer packageResolvedTransformer
    ) {
        this.packageResolvedParser = packageResolvedParser;
        this.packageResolvedFormatChecker = packageResolvedFormatChecker;
        this.packageResolvedTransformer = packageResolvedTransformer;
    }

    public Extraction extract(File foundPackageResolvedFile, File foundCodeLocationFile) throws IOException {
        String packageResolvedContents = FileUtils.readFileToString(foundPackageResolvedFile, Charset.defaultCharset());
        Optional<PackageResolved> packageResolved = packageResolvedParser.parsePackageResolved(packageResolvedContents);
        if (!packageResolved.isPresent()) {
            // There are no dependencies to extract.
            DependencyGraph dependencyGraph = new MutableMapDependencyGraph();
            CodeLocation emptyCodeLocation = new CodeLocation(dependencyGraph, foundCodeLocationFile);
            return new Extraction.Builder().success(emptyCodeLocation).build();
        }

        packageResolvedFormatChecker.checkForVersionCompatibility(
            packageResolved.get(),
            (fileFormatVersion, knownVersions) -> logger.warn(String.format(
                "The format version of Package.resolved (%s) is unknown to Detect, but processing will continue. Known format versions are (%s).",
                fileFormatVersion,
                StringUtils.join(knownVersions, ", ")
            ))
        );

        DependencyGraph dependencyGraph = packageResolvedTransformer.transform(packageResolved.get());
        CodeLocation codeLocation = new CodeLocation(dependencyGraph);

        return new Extraction.Builder()
            .success(codeLocation)
            .build();
    }
}
