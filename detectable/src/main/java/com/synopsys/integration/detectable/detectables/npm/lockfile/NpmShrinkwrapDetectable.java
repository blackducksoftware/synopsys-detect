package com.synopsys.integration.detectable.detectables.npm.lockfile;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExceptionDetectableResult;
import com.synopsys.integration.detectable.detectable.result.NpmPackagesObjectNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.PackageLock;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "NPM Shrinkwrap", language = "Node JS", forge = "npmjs", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "File: npm-shrinkwrap.json. Optionally for better results: package.json also.")
public class NpmShrinkwrapDetectable extends Detectable {
    public static final String SHRINKWRAP_JSON = "npm-shrinkwrap.json";
    public static final String PACKAGE_JSON = "package.json";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FileFinder fileFinder;
    private final NpmLockfileExtractor npmLockfileExtractor;

    private Gson gson;
    private File lockfile;
    private File packageJson;

    public NpmShrinkwrapDetectable(DetectableEnvironment environment, FileFinder fileFinder, NpmLockfileExtractor npmLockfileExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.npmLockfileExtractor = npmLockfileExtractor;
        gson = new Gson();
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        lockfile = requirements.file(SHRINKWRAP_JSON);
        packageJson = requirements.optionalFile(PACKAGE_JSON, () -> logger.warn("Npm applied but it could not find a package.json so dependencies may not be entirely accurate."));
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        try {
            String lockFileText = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);
            PackageLock packageLock = gson.fromJson(lockFileText, PackageLock.class);

            if (packageLock.packages != null) {
                return new PassedDetectableResult();
            } else {
                return new NpmPackagesObjectNotFoundDetectableResult();
            }
        } catch (Exception e) {
            return new ExceptionDetectableResult(e);
        }
    }

    @Override
    public Extraction extract(ExtractionEnvironment environment) {
        return npmLockfileExtractor.extract(lockfile, packageJson);
    }

}
