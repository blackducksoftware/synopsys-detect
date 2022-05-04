package com.synopsys.integration.detect.workflow.codelocation;

import java.io.File;

import org.springframework.lang.Nullable;

import com.synopsys.integration.util.NameVersion;

public class CodeLocationNameManager {
    private final CodeLocationNameGenerator codeLocationNameGenerator;

    public CodeLocationNameManager(CodeLocationNameGenerator codeLocationNameGenerator) {
        this.codeLocationNameGenerator = codeLocationNameGenerator;
    }

    public String createAggregateCodeLocationName(NameVersion projectNameVersion) {
        String aggregateCodeLocationName;
        if (codeLocationNameGenerator.useCodeLocationOverride()) {
            // The aggregate is exclusively used for the bdio and not the scans
            aggregateCodeLocationName = codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.BOM);
        } else {
            aggregateCodeLocationName = String.format("%s/%s Black Duck I/O Export", projectNameVersion.getName(), projectNameVersion.getVersion());
        }
        return aggregateCodeLocationName;
    }

    public String createCodeLocationName(
        DetectCodeLocation detectCodeLocation,
        File detectSourcePath,
        String projectName,
        String projectVersionName
    ) {
        String codeLocationName;
        if (codeLocationNameGenerator.useCodeLocationOverride()) {
            if (detectCodeLocation.getDockerImageName().isPresent()) {
                codeLocationName = codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.DOCKER);
            } else {
                codeLocationName = codeLocationNameGenerator.getNextCodeLocationOverrideNameSourcedBom(detectCodeLocation);
            }
        } else {
            if (detectCodeLocation.getDockerImageName().isPresent()) {
                String dockerImage = detectCodeLocation.getDockerImageName().get();
                codeLocationName = codeLocationNameGenerator.createDockerCodeLocationName(
                    detectCodeLocation.getSourcePath(),
                    projectName,
                    projectVersionName,
                    dockerImage
                );
            } else {
                codeLocationName = codeLocationNameGenerator.createBomCodeLocationName(
                    detectSourcePath,
                    detectCodeLocation.getSourcePath(),
                    projectName,
                    projectVersionName,
                    detectCodeLocation
                );
            }
        }
        return codeLocationName;
    }

    public String createScanCodeLocationName(
        File sourcePath,
        File scanTargetPath,
        @Nullable File dockerTar,
        String projectName,
        String projectVersionName
    ) {
        String scanCodeLocationName;
        if (codeLocationNameGenerator.useCodeLocationOverride()) {
            scanCodeLocationName = codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.SCAN);
        } else if (dockerTar != null) {
            scanCodeLocationName = codeLocationNameGenerator.createDockerScanCodeLocationName(dockerTar, projectName, projectVersionName);
        } else {
            scanCodeLocationName = codeLocationNameGenerator.createScanCodeLocationName(sourcePath, scanTargetPath, projectName, projectVersionName);
        }
        return scanCodeLocationName;
    }

    public String createBinaryScanCodeLocationName(File targetFile, String projectName, String projectVersionName) {
        String scanCodeLocationName;

        if (codeLocationNameGenerator.useCodeLocationOverride()) {
            scanCodeLocationName = codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.SCAN);
        } else {
            scanCodeLocationName = codeLocationNameGenerator.createBinaryScanCodeLocationName(targetFile, projectName, projectVersionName);
        }
        return scanCodeLocationName;
    }

    public String createImpactAnalysisCodeLocationName(File sourceDirectory, String projectName, String projectVersionName) {
        String scanCodeLocationName;

        if (codeLocationNameGenerator.useCodeLocationOverride()) {
            scanCodeLocationName = codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.IMPACT_ANALYSIS);
        } else {
            scanCodeLocationName = codeLocationNameGenerator.createImpactAnalysisCodeLocationName(sourceDirectory, projectName, projectVersionName);
        }
        return scanCodeLocationName;
    }
}
