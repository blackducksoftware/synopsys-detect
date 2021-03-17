/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.cargo;

import java.io.File;
import java.util.Optional;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.detectable.result.CargoLockfileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Rust", forge = "crates", requirementsMarkdown = "Files: Cargo.lock, Cargo.toml")
public class CargoDetectable extends Detectable {
    public static final String CARGO_LOCK_FILENAME = "Cargo.lock";
    public static final String CARGO_TOML_FILENAME = "Cargo.toml";

    private final FileFinder fileFinder;
    private final CargoExtractor cargoExtractor;

    private File cargoLock;
    private File cargoToml;

    public CargoDetectable(DetectableEnvironment environment, FileFinder fileFinder, CargoExtractor cargoExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.cargoExtractor = cargoExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        cargoLock = fileFinder.findFile(environment.getDirectory(), CARGO_LOCK_FILENAME);
        cargoToml = fileFinder.findFile(environment.getDirectory(), CARGO_TOML_FILENAME);
        if (cargoLock == null && cargoToml == null) {
            return new FilesNotFoundDetectableResult(CARGO_LOCK_FILENAME, CARGO_TOML_FILENAME);
        }
        if (cargoLock != null) {
            requirements.explainFile(cargoLock);
        }
        if (cargoToml != null) {
            requirements.explainFile(cargoToml);
        }
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        if (cargoLock == null && cargoToml != null) {
            return new CargoLockfileNotFoundDetectableResult(environment.getDirectory().getAbsolutePath());
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return cargoExtractor.extract(cargoLock, cargoToml);
    }
}
