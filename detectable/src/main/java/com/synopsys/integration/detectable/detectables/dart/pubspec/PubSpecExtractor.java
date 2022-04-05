package com.synopsys.integration.detectable.detectables.dart.pubspec;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.dart.PubSpecYamlNameVersionParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.util.NameVersion;

public class PubSpecExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PubSpecLockParser pubSpecLockParser;
    private final PubSpecYamlNameVersionParser nameVersionParser;

    public PubSpecExtractor(PubSpecLockParser pubSpecLockParser, PubSpecYamlNameVersionParser nameVersionParser) {
        this.pubSpecLockParser = pubSpecLockParser;
        this.nameVersionParser = nameVersionParser;
    }

    public Extraction extract(File pubSpecLockFile, @Nullable File pubSpecYamlFile) throws IOException {
        List<String> pubSpecLockLines = Files.readAllLines(pubSpecLockFile.toPath(), StandardCharsets.UTF_8);

        Optional<NameVersion> nameVersion = Optional.empty();
        if (pubSpecYamlFile != null) {
            List<String> pubSpecYamlLines = Files.readAllLines(pubSpecYamlFile.toPath(), StandardCharsets.UTF_8);
            nameVersion = nameVersionParser.parseNameVersion(pubSpecYamlLines);
        }

        DependencyGraph dependencyGraph = pubSpecLockParser.parse(pubSpecLockLines);

        CodeLocation codeLocation = new CodeLocation(dependencyGraph);
        return new Extraction.Builder().success(codeLocation).nameVersionIfPresent(nameVersion).build();
    }
}
