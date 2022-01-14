package com.synopsys.integration.detectable.detectables.cargo;

import java.io.File;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.result.CargoLockfileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
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
        Requirements requirements = new Requirements(fileFinder, environment); //I don't like it. But I offer no solutions.
        requirements.eitherFile(CARGO_LOCK_FILENAME, CARGO_TOML_FILENAME, foundLock -> cargoLock = foundLock, foundToml -> cargoToml = foundToml);
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
