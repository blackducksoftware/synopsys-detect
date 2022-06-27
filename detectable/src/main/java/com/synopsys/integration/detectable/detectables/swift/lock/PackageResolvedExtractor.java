package com.synopsys.integration.detectable.detectables.swift.lock;

import static com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolvedFormat.V_1;
import static com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolvedFormat.V_2;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;

import com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolvedFormat;
import com.synopsys.integration.detectable.detectables.swift.lock.data.ResolvedPackage;
import com.synopsys.integration.detectable.detectables.swift.lock.data.v1.PackageResolvedV1;
import com.synopsys.integration.detectable.detectables.swift.lock.data.v1.ResolvedObject;
import com.synopsys.integration.detectable.detectables.swift.lock.data.v2.PackageResolvedV2;
import com.synopsys.integration.detectable.detectables.swift.lock.model.PackageResolvedResult;
import com.synopsys.integration.detectable.detectables.swift.lock.parse.PackageResolvedDataChecker;
import com.synopsys.integration.detectable.detectables.swift.lock.parse.PackageResolvedFormatChecker;
import com.synopsys.integration.detectable.detectables.swift.lock.parse.PackageResolvedFormatParser;
import com.synopsys.integration.detectable.detectables.swift.lock.parse.PackageResolvedParser;
import com.synopsys.integration.detectable.detectables.swift.lock.transform.PackageResolvedTransformer;

public class PackageResolvedExtractor {
    private final PackageResolvedParser packageResolvedParser;
    private final PackageResolvedFormatParser packageResolvedFormatParser;
    private final PackageResolvedFormatChecker packageResolvedFormatChecker;
    private final PackageResolvedDataChecker packageResolvedDataChecker;
    private final PackageResolvedTransformer packageResolvedTransformer;

    public PackageResolvedExtractor(
        PackageResolvedParser packageResolvedParser,
        PackageResolvedFormatParser packageResolvedFormatParser,
        PackageResolvedFormatChecker packageResolvedFormatChecker,
        PackageResolvedDataChecker packageResolvedDataChecker,
        PackageResolvedTransformer packageResolvedTransformer
    ) {
        this.packageResolvedParser = packageResolvedParser;
        this.packageResolvedFormatParser = packageResolvedFormatParser;
        this.packageResolvedFormatChecker = packageResolvedFormatChecker;
        this.packageResolvedDataChecker = packageResolvedDataChecker;
        this.packageResolvedTransformer = packageResolvedTransformer;
    }

    public PackageResolvedResult extract(File foundPackageResolvedFile) throws IOException {
        String packageResolvedContents = FileUtils.readFileToString(foundPackageResolvedFile, Charset.defaultCharset());
        PackageResolvedFormat packageResolvedFormat = packageResolvedFormatParser.parseFormatFromJson(packageResolvedContents);

        List<ResolvedPackage> resolvedPackages;
        if (V_1.equals(packageResolvedFormat)) {
            Optional<PackageResolvedV1> packageResolved = packageResolvedParser.parsePackageResolved(packageResolvedContents, PackageResolvedV1.class);
            resolvedPackages = packageResolved
                .map(PackageResolvedV1::getResolvedObject)
                .map(ResolvedObject::getPackages)
                .orElse(null);
        } else {
            if (!V_2.equals(packageResolvedFormat)) {
                // Version might not be supported, will continue with the latest known format
                packageResolvedFormatChecker.checkForVersionCompatibility(packageResolvedFormat);
            }
            Optional<PackageResolvedV2> packageResolved = packageResolvedParser.parsePackageResolved(packageResolvedContents, PackageResolvedV2.class);
            packageResolved.ifPresent(packageResolvedDataChecker::logUnknownPackageTypes);
            resolvedPackages = packageResolved
                .map(PackageResolvedV2::getPackages)
                .orElse(null);
        }

        return Optional.ofNullable(resolvedPackages)
            .map(packageResolvedTransformer::transform)
            .map(PackageResolvedResult::success)
            .orElse(PackageResolvedResult.empty());
    }
}
