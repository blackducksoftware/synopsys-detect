/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pnpm.lockfile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmLockYaml;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class PnpmLockYamlParser {
    private PnpmYamlTransformer pnpmTransformer;

    public PnpmLockYamlParser(PnpmYamlTransformer pnpmTransformer) {
        this.pnpmTransformer = pnpmTransformer;
    }

    public CodeLocation parse(File pnpmLockYamlFile, PnpmDependencyFilter pnpmDependencyFilter, @Nullable NameVersion projectNameVersion) throws IOException, IntegrationException {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(new Constructor(PnpmLockYaml.class), representer);
        PnpmLockYaml pnpmLockYaml = yaml.load(new FileReader(pnpmLockYamlFile));

        return pnpmTransformer.generateCodeLocation(pnpmLockYaml, pnpmDependencyFilter, projectNameVersion);
    }
}
