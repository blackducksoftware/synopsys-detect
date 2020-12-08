package com.synopsys.integration.detectable.detectables.conan.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.conan.ConanCodeLocationGenerator;
import com.synopsys.integration.detectable.detectables.conan.ConanDetectableResult;
import com.synopsys.integration.detectable.detectables.conan.lockfile.parser.ConanLockfileParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;
import com.synopsys.integration.exception.IntegrationException;

@FunctionalTest
public class ConanLockfileParserFunctionalTest {

    @Test
    public void testNoProjectRef() throws IOException, IntegrationException {
        File lockfile = FunctionalTestFiles.asFile("/conan/lockfile/conan.lock");
        lockfile.getAbsolutePath();
        if (lockfile.exists()) {
            System.out.printf("%s exists\n", lockfile.getAbsolutePath());
        }
        ConanCodeLocationGenerator conanCodeLocationGenerator = new ConanCodeLocationGenerator();
        ConanLockfileParser parser = new ConanLockfileParser(conanCodeLocationGenerator);
        String conanLockfileContents = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);
        ConanDetectableResult result = parser.generateCodeLocationFromConanLockfileContents(new Gson(), conanLockfileContents, true);
        assertEquals(3, result.getCodeLocation().getDependencyGraph().getRootDependencies().size());
        Set<ExternalId> rootExternalIds = result.getCodeLocation().getDependencyGraph().getRootDependencyExternalIds();
        for (ExternalId rootExternalId : rootExternalIds) {
            System.out.printf("tbd: %s, %s, %s\n", rootExternalId.getForge(), rootExternalId.getName(), rootExternalId.getVersion());
        }

        DependencyGraph actualDependencyGraph = result.getCodeLocation().getDependencyGraph();

        //        Gson gson = new Gson();
        //        DependencyGraphSummarizer summarizer = new DependencyGraphSummarizer(gson);
        //        GraphSummary actualGraphSummary = summarizer.fromGraph(actualDependencyGraph);
        //        String actualDependencyGraphSummaryJson = gson.toJson(actualGraphSummary);
        //        FileUtils.writeStringToFile(new File("/tmp/t.json"), actualDependencyGraphSummaryJson, StandardCharsets.UTF_8);

        GraphCompare.assertEqualsResource("/conan/lockfile/noProjectRef_graph.json", actualDependencyGraph);
    }

    @Test
    public void testProjectRef() throws IOException, IntegrationException {
        File lockfile = FunctionalTestFiles.asFile("/conan/lockfile/conan_projectref.lock");
        lockfile.getAbsolutePath();
        if (lockfile.exists()) {
            System.out.printf("%s exists\n", lockfile.getAbsolutePath());
        }
        ConanCodeLocationGenerator conanCodeLocationGenerator = new ConanCodeLocationGenerator();
        ConanLockfileParser parser = new ConanLockfileParser(conanCodeLocationGenerator);
        String conanLockfileContents = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);
        ConanDetectableResult result = parser.generateCodeLocationFromConanLockfileContents(new Gson(), conanLockfileContents, true);

        assertEquals("chat", result.getProjectName());
        assertEquals("0.1", result.getProjectVersion());
    }

    @Test
    public void testRelativePath() throws IOException, IntegrationException {
        File lockfile = FunctionalTestFiles.asFile("/conan/lockfile/conan_relpath.lock");
        lockfile.getAbsolutePath();
        if (lockfile.exists()) {
            System.out.printf("%s exists\n", lockfile.getAbsolutePath());
        }
        ConanCodeLocationGenerator conanCodeLocationGenerator = new ConanCodeLocationGenerator();
        ConanLockfileParser parser = new ConanLockfileParser(conanCodeLocationGenerator);
        String conanLockfileContents = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);
        ConanDetectableResult result = parser.generateCodeLocationFromConanLockfileContents(new Gson(), conanLockfileContents, true);

        assertEquals(1, result.getCodeLocation().getDependencyGraph().getRootDependencies().size());
    }
}
