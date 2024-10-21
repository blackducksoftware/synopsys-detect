package com.blackduck.integration.detectable.detectables.go.vendr;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackduck.integration.bdio.graph.DependencyGraph;
import com.blackduck.integration.bdio.model.externalid.ExternalIdFactory;
import com.blackduck.integration.detectable.detectable.codelocation.CodeLocation;
import com.blackduck.integration.detectable.detectables.go.vendr.parse.VndrParser;
import com.blackduck.integration.detectable.extraction.Extraction;

public class GoVndrExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public GoVndrExtractor(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public Extraction extract(File vndrConfig) {
        try {
            VndrParser vndrParser = new VndrParser(externalIdFactory);
            List<String> vendorConfContents = Files.readAllLines(vndrConfig.toPath(), StandardCharsets.UTF_8);
            logger.debug(String.join("\n", vendorConfContents));
            DependencyGraph dependencyGraph = vndrParser.parseVendorConf(vendorConfContents);
            CodeLocation codeLocation = new CodeLocation(dependencyGraph);
            return new Extraction.Builder().success(codeLocation).build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
