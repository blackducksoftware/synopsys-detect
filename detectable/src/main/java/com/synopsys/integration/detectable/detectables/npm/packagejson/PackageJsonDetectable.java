package com.synopsys.integration.detectable.detectables.npm.packagejson;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;

public class PackageJsonDetectable extends Detectable {
    public PackageJsonDetectable(final DetectableEnvironment environment) {
        super(environment, "package.json", "NPM");
    }

    @Override
    public DetectableResult applicable() {
        return null;
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        return null;
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return null;
    }
}
