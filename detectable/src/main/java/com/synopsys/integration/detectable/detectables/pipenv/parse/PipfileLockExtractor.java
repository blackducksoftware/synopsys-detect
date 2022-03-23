package com.synopsys.integration.detectable.detectables.pipenv.parse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.pipenv.parse.data.PipfileLock;
import com.synopsys.integration.detectable.detectables.pipenv.parse.model.PipfileLockDependency;
import com.synopsys.integration.detectable.extraction.Extraction;

public class PipfileLockExtractor {
    private final Gson gson;
    private final PipfileLockParser pipfileLockParser;
    private final PipfileLockDependencyTransformer pipfileLockTransformer;

    public PipfileLockExtractor(
        Gson gson,
        PipfileLockParser pipfileLockParser,
        PipfileLockDependencyTransformer pipfileLockTransformer
    ) {
        this.gson = gson;
        this.pipfileLockParser = pipfileLockParser;
        this.pipfileLockTransformer = pipfileLockTransformer;
    }

    public Extraction extract(File pipfileLockFile) throws IOException {
        String pipfileLockText = FileUtils.readFileToString(pipfileLockFile, StandardCharsets.UTF_8);
        PipfileLock pipfileLock = gson.fromJson(pipfileLockText, PipfileLock.class);
        List<PipfileLockDependency> dependencies = pipfileLockParser.parse(pipfileLock);
        DependencyGraph dependencyGraph = pipfileLockTransformer.transform(dependencies);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph);
        // No project info - hoping git can help with that.
        return Extraction.success(codeLocation);
    }
}
