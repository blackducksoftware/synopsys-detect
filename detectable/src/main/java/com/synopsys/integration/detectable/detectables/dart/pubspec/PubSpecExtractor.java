/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.dart.pubspec;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.dart.PubSpecYamlNameVersionParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.util.NameVersion;

public class PubSpecExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private PubSpecLockParser pubSpecLockParser;
    private PubSpecYamlNameVersionParser nameVersionParser;

    public PubSpecExtractor(PubSpecLockParser pubSpecLockParser, PubSpecYamlNameVersionParser nameVersionParser) {
        this.pubSpecLockParser = pubSpecLockParser;
        this.nameVersionParser = nameVersionParser;
    }

    public Extraction extract(File pubSpecLockFile, File pubSpecYamlFile) {
        try {
            List<String> pubSpecLockLines = Files.readAllLines(pubSpecLockFile.toPath(), StandardCharsets.UTF_8);
            logger.debug(String.join(System.lineSeparator(), pubSpecLockLines));

            Optional<NameVersion> nameVersion = nameVersionParser.parseNameVersion(pubSpecYamlFile);

            DependencyGraph dependencyGraph = pubSpecLockParser.parse(pubSpecLockLines);

            CodeLocation codeLocation = new CodeLocation(dependencyGraph);
            return new Extraction.Builder().success(codeLocation).nameVersionIfPresent(nameVersion).build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
