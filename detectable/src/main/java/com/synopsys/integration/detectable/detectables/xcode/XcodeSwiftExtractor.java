package com.synopsys.integration.detectable.detectables.xcode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.io.FileUtils;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.xcode.data.PackageResolved;
import com.synopsys.integration.detectable.detectables.xcode.parse.PackageResolvedParser;
import com.synopsys.integration.detectable.detectables.xcode.process.PackageResolvedTransformer;
import com.synopsys.integration.detectable.extraction.Extraction;

public class XcodeSwiftExtractor {
    private final PackageResolvedParser packageResolvedParser;
    private final PackageResolvedTransformer packageResolvedTransformer;

    public XcodeSwiftExtractor(
        PackageResolvedParser packageResolvedParser,
        PackageResolvedTransformer packageResolvedTransformer
    ) {
        this.packageResolvedParser = packageResolvedParser;
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

        DependencyGraph dependencyGraph = packageResolvedTransformer.transform(packageResolved.get());
        CodeLocation codeLocation = new CodeLocation(dependencyGraph);

        return new Extraction.Builder()
            .success(codeLocation)
            .build();
    }
}
