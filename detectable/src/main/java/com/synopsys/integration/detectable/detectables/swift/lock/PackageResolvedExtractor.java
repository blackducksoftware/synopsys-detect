package com.synopsys.integration.detectable.detectables.swift.lock;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.io.FileUtils;

import com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolved;
import com.synopsys.integration.detectable.detectables.swift.lock.model.PackageResolvedResult;
import com.synopsys.integration.detectable.detectables.swift.lock.parse.PackageResolvedFormatChecker;
import com.synopsys.integration.detectable.detectables.swift.lock.parse.PackageResolvedParser;
import com.synopsys.integration.detectable.detectables.xcode.transform.PackageResolvedTransformer;

public class PackageResolvedExtractor {
    private final PackageResolvedParser packageResolvedParser;
    private final PackageResolvedFormatChecker packageResolvedFormatChecker;
    private final PackageResolvedTransformer packageResolvedTransformer;

    public PackageResolvedExtractor(
        PackageResolvedParser packageResolvedParser,
        PackageResolvedFormatChecker packageResolvedFormatChecker,
        PackageResolvedTransformer packageResolvedTransformer
    ) {
        this.packageResolvedParser = packageResolvedParser;
        this.packageResolvedFormatChecker = packageResolvedFormatChecker;
        this.packageResolvedTransformer = packageResolvedTransformer;
    }

    public PackageResolvedResult extract(File foundPackageResolvedFile) throws IOException {
        String packageResolvedContents = FileUtils.readFileToString(foundPackageResolvedFile, Charset.defaultCharset());
        Optional<PackageResolved> packageResolved = packageResolvedParser.parsePackageResolved(packageResolvedContents);
        packageResolved.ifPresent(packageResolvedFormatChecker::checkForVersionCompatibility);
        return packageResolved
            .map(packageResolvedTransformer::transform)
            .map(PackageResolvedResult::success)
            .orElse(PackageResolvedResult.empty());
    }
}
