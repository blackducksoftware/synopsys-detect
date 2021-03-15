/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.codelocation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.workflow.file.DetectFileUtils;

public class CodeLocationNameGenerator {
    private final String codeLocationNameOverride;
    private final Map<String, Integer> nameCounters = new HashMap<>();
    private static final int MAXIMUM_CODE_LOCATION_NAME_LENGTH = 250;

    public CodeLocationNameGenerator(@Nullable String codeLocationNameOverride) {
        this.codeLocationNameOverride = codeLocationNameOverride;
    }

    public String createBomCodeLocationName(File detectSourcePath, File sourcePath, String projectName, String projectVersionName, DetectCodeLocation detectCodeLocation, @Nullable String prefix, @Nullable String suffix) {
        String canonicalDetectSourcePath = DetectFileUtils.tryGetCanonicalPath(detectSourcePath);
        String canonicalSourcePath = DetectFileUtils.tryGetCanonicalPath(sourcePath);
        String pathPiece = FileNameUtils.relativize(canonicalDetectSourcePath, canonicalSourcePath);

        String externalIdPiece = StringUtils.join(detectCodeLocation.getExternalId().getExternalIdPieces(), "/");

        // misc pieces
        String codeLocationTypeString = CodeLocationNameType.BOM.toString().toLowerCase();
        String bomToolTypeString = deriveCreator(detectCodeLocation).toLowerCase();

        List<String> bomCodeLocationNamePieces = new ArrayList<>();
        bomCodeLocationNamePieces.add(projectName);
        bomCodeLocationNamePieces.add(projectVersionName);
        if (StringUtils.isNotBlank(pathPiece)) {
            bomCodeLocationNamePieces.add(pathPiece);
        }
        bomCodeLocationNamePieces.add(externalIdPiece);
        List<String> bomCodeLocationEndPieces = Arrays.asList(bomToolTypeString, codeLocationTypeString);

        return createCodeLocationName(prefix, bomCodeLocationNamePieces, suffix, bomCodeLocationEndPieces);
    }

    public String createDockerCodeLocationName(File sourcePath, String projectName, String projectVersionName, String dockerImage, @Nullable String prefix, @Nullable String suffix) {
        String canonicalSourcePath = DetectFileUtils.tryGetCanonicalPath(sourcePath);
        String finalSourcePathPiece = DetectFileUtils.extractFinalPieceFromPath(canonicalSourcePath);
        String codeLocationTypeString = CodeLocationNameType.DOCKER.toString().toLowerCase();
        final String bomToolTypeString = "docker";

        List<String> dockerCodeLocationNamePieces = Arrays.asList(finalSourcePathPiece, projectName, projectVersionName, dockerImage);
        List<String> dockerCodeLocationEndPieces = Arrays.asList(codeLocationTypeString, bomToolTypeString);

        return createCodeLocationName(prefix, dockerCodeLocationNamePieces, suffix, dockerCodeLocationEndPieces);
    }

    public String createDockerScanCodeLocationName(File dockerTar, String projectName, String projectVersionName, @Nullable String prefix, @Nullable String suffix) {
        String codeLocationTypeString = CodeLocationNameType.SCAN.toString().toLowerCase();

        String dockerTarFileName = DetectFileUtils.tryGetCanonicalName(dockerTar);
        List<String> fileCodeLocationNamePieces = Arrays.asList(dockerTarFileName, projectName, projectVersionName);
        List<String> fileCodeLocationEndPieces = Collections.singletonList(codeLocationTypeString);

        return createCodeLocationName(prefix, fileCodeLocationNamePieces, suffix, fileCodeLocationEndPieces);
    }

    public String createScanCodeLocationName(File sourcePath, File scanTargetPath, String projectName, String projectVersionName, @Nullable String prefix, @Nullable String suffix) {
        String pathPiece = cleanScanTargetPath(scanTargetPath, sourcePath);
        String codeLocationTypeString = CodeLocationNameType.SCAN.toString().toLowerCase();

        List<String> fileCodeLocationNamePieces = Arrays.asList(pathPiece, projectName, projectVersionName);
        List<String> fileCodeLocationEndPieces = Collections.singletonList(codeLocationTypeString);

        return createCodeLocationName(prefix, fileCodeLocationNamePieces, suffix, fileCodeLocationEndPieces);
    }

    public String createBinaryScanCodeLocationName(File targetFile, String projectName, String projectVersionName, @Nullable String prefix, @Nullable String suffix) {
        String codeLocationTypeString = CodeLocationNameType.SCAN.toString().toLowerCase();

        String canonicalFileName = DetectFileUtils.tryGetCanonicalName(targetFile);
        List<String> fileCodeLocationNamePieces = Arrays.asList(canonicalFileName, projectName, projectVersionName);
        List<String> fileCodeLocationEndPieces = Collections.singletonList(codeLocationTypeString);

        return createCodeLocationName(prefix, fileCodeLocationNamePieces, suffix, fileCodeLocationEndPieces);
    }

    public String createImpactAnalysisCodeLocationName(File sourceDirectory, String projectName, String projectVersionName, @Nullable String prefix, @Nullable String suffix) {
        String codeLocationTypeString = CodeLocationNameType.IMPACT_ANALYSIS.toString().toLowerCase();

        String canonicalFileName = DetectFileUtils.tryGetCanonicalName(sourceDirectory);
        List<String> fileCodeLocationNamePieces = Arrays.asList(canonicalFileName, projectName, projectVersionName);
        List<String> fileCodeLocationEndPieces = Collections.singletonList(codeLocationTypeString);

        return createCodeLocationName(prefix, fileCodeLocationNamePieces, suffix, fileCodeLocationEndPieces);
    }

    private String createCodeLocationName(@Nullable String prefix, List<String> codeLocationNamePieces, @Nullable String suffix, List<String> codeLocationEndPieces) {
        String codeLocationName = createCommonName(prefix, codeLocationNamePieces, suffix, codeLocationEndPieces);

        if (codeLocationName.length() > MAXIMUM_CODE_LOCATION_NAME_LENGTH) {
            codeLocationName = createShortenedCodeLocationName(codeLocationNamePieces, prefix, suffix, codeLocationEndPieces);
        }

        return codeLocationName;
    }

    private String cleanScanTargetPath(File scanTargetPath, File sourcePath) {
        String canonicalTargetPath = DetectFileUtils.tryGetCanonicalPath(scanTargetPath);
        String canonicalSourcePath = DetectFileUtils.tryGetCanonicalPath(sourcePath);

        String finalSourcePathPiece = DetectFileUtils.extractFinalPieceFromPath(canonicalSourcePath);
        String cleanedTargetPath = "";
        if (StringUtils.isNotBlank(canonicalTargetPath) && StringUtils.isNotBlank(finalSourcePathPiece)) {
            cleanedTargetPath = canonicalTargetPath.replace(canonicalSourcePath, finalSourcePathPiece);
        }

        return cleanedTargetPath;
    }

    private String createShortenedCodeLocationName(List<String> namePieces, @Nullable String prefix, @Nullable String suffix, List<String> endPieces) {
        List<String> shortenedNamePieces = namePieces.stream().map(this::shortenPiece).collect(Collectors.toList());

        String shortenedPrefix = shortenPiece(prefix);
        String shortenedSuffix = shortenPiece(suffix);

        return createCommonName(shortenedPrefix, shortenedNamePieces, shortenedSuffix, endPieces);
    }

    private String shortenPiece(@Nullable String piece) {
        if (piece == null || piece.length() <= 40) {
            return piece;
        } else {
            return piece.substring(0, 19) + "..." + piece.substring(piece.length() - 18);
        }
    }

    private String createCommonName(@Nullable String prefix, List<String> namePieces, @Nullable String suffix, List<String> endPieces) {
        ArrayList<String> commonNamePieces = new ArrayList<>();

        if (StringUtils.isNotBlank(prefix)) {
            commonNamePieces.add(prefix);
        }

        commonNamePieces.addAll(namePieces);

        if (StringUtils.isNotBlank(suffix)) {
            commonNamePieces.add(suffix);
        }

        String name = StringUtils.join(commonNamePieces, "/");
        String endPiece = StringUtils.join(endPieces, "/");

        return String.format("%s %s", name, endPiece);
    }

    public boolean useCodeLocationOverride() {
        return StringUtils.isNotBlank(codeLocationNameOverride);
    }

    public String getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType codeLocationNameType) {
        String baseName = codeLocationNameOverride + " " + codeLocationNameType.toString().toLowerCase();
        int nameIndex = deriveNameNumber(baseName);
        String nextName = deriveUniqueCodeLocationName(baseName, nameIndex);
        return nextName;
    }

    public String getNextCodeLocationOverrideNameSourcedBom(DetectCodeLocation detectCodeLocation) {
        String creator = deriveCreator(detectCodeLocation);
        String baseName = createBomCodeLocationName(codeLocationNameOverride, creator);

        int nameIndex = deriveNameNumber(baseName);
        String nextName = deriveUniqueCodeLocationName(baseName, nameIndex);
        return nextName;
    }

    public String deriveCreator(DetectCodeLocation detectCodeLocation) {
        return detectCodeLocation.getCreatorName().orElse("detect");
    }

    private String createBomCodeLocationName(String givenCodeLocationName, String creatorName) {
        String codeLocationTypeString = CodeLocationNameType.BOM.toString().toLowerCase();
        String bomToolTypeString = creatorName.toLowerCase();

        int givenNameMaxLength = MAXIMUM_CODE_LOCATION_NAME_LENGTH - bomToolTypeString.length() - codeLocationTypeString.length() - 2;
        String adjustedGivenCodeLocationName;
        if (givenCodeLocationName.length() > givenNameMaxLength) {
            adjustedGivenCodeLocationName = givenCodeLocationName.substring(0, givenNameMaxLength);
        } else {
            adjustedGivenCodeLocationName = givenCodeLocationName;
        }
        String codeLocationName = String.format("%s %s/%s", adjustedGivenCodeLocationName, bomToolTypeString, codeLocationTypeString);

        return codeLocationName;
    }

    private String deriveUniqueCodeLocationName(String baseName, int nameIndex) {
        String nextName;
        if (nameIndex > 1) {
            nextName = baseName + " " + nameIndex;
        } else {
            nextName = baseName;
        }
        return nextName;
    }

    private int deriveNameNumber(String baseName) {
        int nameIndex;
        if (nameCounters.containsKey(baseName)) {
            nameIndex = nameCounters.get(baseName);
            nameIndex++;
        } else {
            nameIndex = 1;
        }
        nameCounters.put(baseName, nameIndex);
        return nameIndex;
    }

}
