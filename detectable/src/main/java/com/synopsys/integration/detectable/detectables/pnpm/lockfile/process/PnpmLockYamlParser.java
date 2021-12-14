package com.synopsys.integration.detectable.detectables.pnpm.lockfile.process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmLockYaml;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmProjectPackage;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class PnpmLockYamlParser {
    private final PnpmYamlTransformer pnpmTransformer;

    public PnpmLockYamlParser(PnpmYamlTransformer pnpmTransformer) {
        this.pnpmTransformer = pnpmTransformer;
    }

    public List<CodeLocation> parse(File pnpmLockYamlFile, @Nullable NameVersion projectNameVersion, PnpmLinkedPackageResolver linkedPackageResolver) throws IOException, IntegrationException {
        PnpmLockYaml pnpmLockYaml = parseYamlFile(pnpmLockYamlFile);
        List<CodeLocation> codeLocationsFromImports = createCodeLocationsFromImports(pnpmLockYamlFile, pnpmLockYaml, linkedPackageResolver, projectNameVersion);
        if (codeLocationsFromImports.isEmpty()) {
            return Collections.singletonList(pnpmTransformer.generateCodeLocation(pnpmLockYamlFile, pnpmLockYaml, projectNameVersion, linkedPackageResolver));
        }
        return codeLocationsFromImports;
    }

    private List<CodeLocation> createCodeLocationsFromImports(File pnpmLockYamlFile, PnpmLockYaml pnpmLockYaml, PnpmLinkedPackageResolver linkedPackageResolver, @Nullable NameVersion projectNameVersion) throws IntegrationException {
        if (MapUtils.isEmpty(pnpmLockYaml.importers)) {
            return Collections.emptyList();
        }

        List<CodeLocation> codeLocations = new LinkedList<>();
        for (Map.Entry<String, PnpmProjectPackage> projectPackageInfo : pnpmLockYaml.importers.entrySet()) {
            PnpmProjectPackage projectPackage = projectPackageInfo.getValue();
            NameVersion extractedNameVersion = extractProjectInfo(projectPackageInfo, linkedPackageResolver, projectNameVersion);

            String reportingProjectPackagePath = null;
            if (extractedNameVersion.equals(projectNameVersion)) {
                reportingProjectPackagePath = projectPackageInfo.getKey();
            }
            File sourcePath = generateCodeLocationSourcePath(pnpmLockYamlFile, reportingProjectPackagePath).orElse(null);
            codeLocations.add(pnpmTransformer.generateCodeLocation(sourcePath, projectPackage, reportingProjectPackagePath, extractedNameVersion, pnpmLockYaml.packages, linkedPackageResolver));
        }

        return codeLocations;
    }

    private NameVersion extractProjectInfo(Map.Entry<String, PnpmProjectPackage> projectPackageInfo, PnpmLinkedPackageResolver linkedPackageResolver, @Nullable NameVersion projectNameVersion) {
        if (projectPackageInfo.getKey().equals(".") && projectNameVersion != null && projectNameVersion.getName() != null) {
            // resolve "." package to project root
            return projectNameVersion;
        }

        String projectPackageName = projectPackageInfo.getKey();
        String projectPackageVersion = linkedPackageResolver.resolveVersionOfLinkedPackage(null, projectPackageName);
        return new NameVersion(projectPackageName, projectPackageVersion);
    }

    private PnpmLockYaml parseYamlFile(File pnpmLockYamlFile) throws FileNotFoundException {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(new Constructor(PnpmLockYaml.class), representer);
        return yaml.load(new FileReader(pnpmLockYamlFile));
    }

    private Optional<File> generateCodeLocationSourcePath(File pnpmLockYamlFile, @Nullable String reportingProjectPackagePath) {
        if (StringUtils.isNotEmpty(reportingProjectPackagePath)) {
            File reportingProjectFile = new File(pnpmLockYamlFile.getParent(), reportingProjectPackagePath);
            if (reportingProjectFile.exists()) {
                return Optional.of(reportingProjectFile);
            }
        }
        return Optional.empty();
    }
}
