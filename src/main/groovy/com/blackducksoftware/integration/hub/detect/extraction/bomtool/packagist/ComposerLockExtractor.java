package com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist.parse.PackagistParseResult;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist.parse.PackagistParser;

@Component
public class ComposerLockExtractor extends Extractor<ComposerLockContext> {

    @Autowired
    PackagistParser packagistParser;

    @Override
    public Extraction extract(final ComposerLockContext context) {
        try {
            final String composerJsonText = FileUtils.readFileToString(context.composerJson, StandardCharsets.UTF_8);
            final String composerLockText = FileUtils.readFileToString(context.composerLock, StandardCharsets.UTF_8);

            final PackagistParseResult result = packagistParser.getDependencyGraphFromProject(context.directory.toString(), composerJsonText, composerLockText);

            return new Extraction.Builder().success(result.codeLocation).projectName(result.projectName).projectVersion(result.projectVersion).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
