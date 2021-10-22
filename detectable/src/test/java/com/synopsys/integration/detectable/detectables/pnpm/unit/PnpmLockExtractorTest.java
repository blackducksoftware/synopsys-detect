package com.synopsys.integration.detectable.detectables.pnpm.unit;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmLockExtractor;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmLockYamlParser;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmYamlTransformer;

public class PnpmLockExtractorTest {
    PnpmLockExtractor extractor = new PnpmLockExtractor(new Gson(), new PnpmLockYamlParser(new PnpmYamlTransformer(new ExternalIdFactory())));

    @Test
    public void testNoFailureOnNullPackageJson() {
        extractor.extract(new File("pnpm-lock.yaml"), null, true, true);
    }
}
