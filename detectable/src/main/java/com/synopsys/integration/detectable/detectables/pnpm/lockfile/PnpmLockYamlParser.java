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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.enums.DependencyType;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmLockYaml;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmProjectPackage;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class PnpmLockYamlParser {
    private PnpmYamlTransformer pnpmTransformer;

    public PnpmLockYamlParser(PnpmYamlTransformer pnpmTransformer) {
        this.pnpmTransformer = pnpmTransformer;
    }

    //TODO- can we improve this by utilizing streams?
    public List<CodeLocation> parse(File pnpmLockYamlFile, List<DependencyType> dependencyTypes, @Nullable NameVersion projectNameVersion, PnpmLinkedPackageResolver linkedPackageResolver) throws IOException, IntegrationException {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(new Constructor(PnpmLockYaml.class), representer);
        PnpmLockYaml pnpmLockYaml = yaml.load(new FileReader(pnpmLockYamlFile));
        List<CodeLocation> codeLocations = new LinkedList<>();
        if (MapUtils.isNotEmpty(pnpmLockYaml.importers)) {
            for (Map.Entry<String, PnpmProjectPackage> projectPackageInfo : pnpmLockYaml.importers.entrySet()) {
                String projectPackageName;
                String projectPackageVersion;
                String reportingProjectPackagePath;
                if (projectPackageInfo.getKey().equals(".") && projectNameVersion != null && projectNameVersion.getName() != null) {
                    // resolve "." package to project root
                    projectPackageName = projectNameVersion.getName();
                    projectPackageVersion = projectNameVersion.getVersion();
                    reportingProjectPackagePath = null;
                } else {
                    projectPackageName = projectPackageInfo.getKey();
                    projectPackageVersion = linkedPackageResolver.resolveVersionOfLinkedPackage(null, projectPackageName);
                    reportingProjectPackagePath = projectPackageName;
                }
                PnpmProjectPackage projectPackage = projectPackageInfo.getValue();
                codeLocations.add(pnpmTransformer.generateCodeLocation(projectPackage, reportingProjectPackagePath, dependencyTypes, new NameVersion(projectPackageName, projectPackageVersion), pnpmLockYaml.packages, linkedPackageResolver));
            }
        }
        try {
            codeLocations.add(pnpmTransformer.generateCodeLocation(pnpmLockYaml, dependencyTypes, projectNameVersion, linkedPackageResolver));
        } catch (DetectableException e) {
            if (!codeLocations.isEmpty()) {
                // If lock file is structured such that all root dependencies are declared in importers section, ignore exception
            } else {
                throw e;
            }
        }
        return codeLocations;
    }
}
