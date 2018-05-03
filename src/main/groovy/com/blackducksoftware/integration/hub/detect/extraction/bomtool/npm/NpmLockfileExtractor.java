package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmLockfilePackager;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

@Component
public class NpmLockfileExtractor extends Extractor<NpmLockfileContext> {

    @Autowired
    private NpmLockfilePackager npmLockfilePackager;

    @Autowired
    protected DetectConfiguration detectConfiguration;

    @Override
    public Extraction extract(final NpmLockfileContext context) {
        String lockText;
        try {
            lockText = FileUtils.readFileToString(context.lockfile, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            return new Extraction(ExtractionResult.Failure, e);
        }

        DetectCodeLocation detectCodeLocation;
        try {
            final boolean includeDev = detectConfiguration.getNpmIncludeDevDependencies();
            detectCodeLocation = npmLockfilePackager.parse(context.directory.getCanonicalPath(), lockText, includeDev);
        } catch (final IOException e) {
            return new Extraction(ExtractionResult.Failure, e);
        }

        return new Extraction(ExtractionResult.Success, detectCodeLocation);
    }
}
