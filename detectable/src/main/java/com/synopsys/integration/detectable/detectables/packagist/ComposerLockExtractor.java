package com.synopsys.integration.detectable.detectables.packagist;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.synopsys.integration.detectable.detectables.packagist.model.PackagistParseResult;
import com.synopsys.integration.detectable.detectables.packagist.parse.PackagistParser;
import com.synopsys.integration.detectable.extraction.Extraction;

public class ComposerLockExtractor {
    private final PackagistParser packagistParser;

    public ComposerLockExtractor(PackagistParser packagistParser) {
        this.packagistParser = packagistParser;
    }

    public Extraction extract(File composerJson, File composerLock) {
        try {
            String composerJsonText = FileUtils.readFileToString(composerJson, StandardCharsets.UTF_8);
            String composerLockText = FileUtils.readFileToString(composerLock, StandardCharsets.UTF_8);
            PackagistParseResult result = packagistParser.getDependencyGraphFromProject(composerJsonText, composerLockText);

            return new Extraction.Builder()
                .success(result.getCodeLocation())
                .projectName(result.getProjectName())
                .projectVersion(result.getProjectVersion())
                .build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
