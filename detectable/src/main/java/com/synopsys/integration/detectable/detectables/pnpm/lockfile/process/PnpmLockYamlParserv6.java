package com.synopsys.integration.detectable.detectables.pnpm.lockfile.process;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmLockYamlv6;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmProjectPackagev6;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class PnpmLockYamlParserv6 {
    private static final Predicate<String> isNodeRoot = "."::equals;

    private PnpmYamlTransformerv6 pnpmTransformer;

    public PnpmLockYamlParserv6(PnpmYamlTransformerv6 pnpmTransformer) {
        this.pnpmTransformer = pnpmTransformer;
    }

    public List<CodeLocation> parse(File parentFile, PnpmLockYamlv6 pnpmLockYaml,
            PnpmLinkedPackageResolver linkedPackageResolver, @Nullable NameVersion projectNameVersion)
            throws IntegrationException {

        List<CodeLocation> codeLocationsFromImports = createCodeLocationsFromImports(parentFile, pnpmLockYaml,
                linkedPackageResolver, projectNameVersion);
        if (codeLocationsFromImports.isEmpty()) {
            return createCodeLocationsFromRoot(parentFile, pnpmLockYaml, projectNameVersion, linkedPackageResolver);
        }
        return codeLocationsFromImports;
    }

    private List<CodeLocation> createCodeLocationsFromRoot(File sourcePath, PnpmLockYamlv6 pnpmLockYaml,
            @Nullable NameVersion projectNameVersion, PnpmLinkedPackageResolver linkedPackageResolver)
            throws IntegrationException {
        CodeLocation codeLocation = pnpmTransformer.generateCodeLocation(sourcePath, pnpmLockYaml, projectNameVersion,
                linkedPackageResolver);
        return Collections.singletonList(codeLocation);
    }

    private List<CodeLocation> createCodeLocationsFromImports(File sourcePath, PnpmLockYamlv6 pnpmLockYaml,
            PnpmLinkedPackageResolver linkedPackageResolver, @Nullable NameVersion projectNameVersion)
            throws IntegrationException {
        if (MapUtils.isEmpty(pnpmLockYaml.importers)) {
            return Collections.emptyList();
        }

        List<CodeLocation> codeLocations = new LinkedList<>();
        for (Map.Entry<String, PnpmProjectPackagev6> projectPackageInfo : pnpmLockYaml.importers.entrySet()) {
            String projectKey = projectPackageInfo.getKey();
            PnpmProjectPackagev6 projectPackage = projectPackageInfo.getValue();
            NameVersion extractedNameVersion = extractProjectInfo(projectPackageInfo, linkedPackageResolver,
                    projectNameVersion);

            String reportingProjectPackagePath = null;
            if (!isNodeRoot.evaluate(projectKey)) {
                reportingProjectPackagePath = projectKey;
            }
            File generatedSourcePath = generateCodeLocationSourcePath(sourcePath, reportingProjectPackagePath);

            codeLocations.add(pnpmTransformer.generateCodeLocation(generatedSourcePath, projectPackage,
                    reportingProjectPackagePath, extractedNameVersion, pnpmLockYaml.packages, linkedPackageResolver));
        }

        return codeLocations;
    }

    private NameVersion extractProjectInfo(Map.Entry<String, PnpmProjectPackagev6> projectPackageInfo,
            PnpmLinkedPackageResolver linkedPackageResolver, @Nullable NameVersion projectNameVersion) {
        if (isNodeRoot.evaluate(projectPackageInfo.getKey()) && projectNameVersion != null
                && projectNameVersion.getName() != null) {
            // resolve "." package to project root
            return projectNameVersion;
        }

        String projectPackageName = projectPackageInfo.getKey();
        String projectPackageVersion = linkedPackageResolver.resolveVersionOfLinkedPackage(null, projectPackageName);
        return new NameVersion(projectPackageName, projectPackageVersion);
    }

    private File generateCodeLocationSourcePath(File sourcePath, @Nullable String reportingProjectPackagePath) {
        if (StringUtils.isNotEmpty(reportingProjectPackagePath)) {
            return new File(sourcePath, reportingProjectPackagePath);
        }
        return sourcePath;
    }
}
