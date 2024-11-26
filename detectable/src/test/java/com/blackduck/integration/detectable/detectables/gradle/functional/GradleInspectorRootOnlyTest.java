package com.blackduck.integration.detectable.detectables.gradle.functional;

import com.blackduck.integration.bdio.model.Forge;
import com.blackduck.integration.bdio.model.dependency.Dependency;
import com.blackduck.integration.bdio.model.externalid.ExternalId;
import com.blackduck.integration.bdio.model.externalid.ExternalIdFactory;
import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.detectable.codelocation.CodeLocation;
import com.blackduck.integration.detectable.detectable.util.EnumListFilter;
import com.blackduck.integration.detectable.detectables.gradle.inspection.GradleInspectorOptions;
import com.blackduck.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptOptions;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.functional.DetectableFunctionalTest;
import com.blackduck.integration.detectable.util.ExtractionUtil;
import com.blackduck.integration.detectable.util.FunctionalTestFiles;
import com.blackduck.integration.detectable.util.graph.NameVersionGraphAssert;
import com.blackduck.integration.executable.ExecutableOutput;
import com.blackduck.integration.rest.proxy.ProxyInfo;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GradleInspectorRootOnlyTest extends DetectableFunctionalTest {
    protected GradleInspectorRootOnlyTest() throws IOException {
        super("my-root-only-test-attempt");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("build.gradle"), "no content");

        ExecutableOutput gradleDependenciesOutput = createStandardOutput("no content");

        addExecutableOutput(
                gradleDependenciesOutput,
                new File("gradle").getCanonicalPath(),
                "gatherDependencies",
                "--init-script=gradle-inspector",
                "-DGRADLEEXTRACTIONDIR=" + getOutputDirectory().toFile().getCanonicalPath(),
                "--info"
        );

        List<String> lines = FunctionalTestFiles.asListOfStrings("/gradle/root-only-tests/rootProjectMetadata.txt");
        addOutputFile(Paths.get("rootProjectMetadata.txt"), lines);

        List<String> newLines = FunctionalTestFiles.asListOfStrings("/gradle/root-only-tests/root_project__simple__depth0_dependencyGraph.txt");
        addOutputFile(Paths.get("root_dependencyGraph.txt"), newLines);
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        GradleInspectorOptions gradleInspectorOptions = new GradleInspectorOptions("",
                new GradleInspectorScriptOptions(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        "",
                        true
                ),
                ProxyInfo.NO_PROXY_INFO, EnumListFilter.excludeNone()
        );
        return detectableFactory.createGradleDetectable(
                detectableEnvironment,
                gradleInspectorOptions,
                () -> new File("gradle-inspector"),
                (environment) -> ExecutableTarget.forFile(new File("gradle"))
        );
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(4, extraction.getCodeLocations().size());

        CodeLocation root = ExtractionUtil.assertAndGetCodeLocationNamed("simple", extraction);
        CodeLocation subProjectA = ExtractionUtil.assertAndGetCodeLocationNamed("subProjectA", extraction);
        CodeLocation subProjectB = ExtractionUtil.assertAndGetCodeLocationNamed("subProjectB", extraction);
        CodeLocation subProjectC = ExtractionUtil.assertAndGetCodeLocationNamed("subProjectC", extraction);

        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        // Root dependencies
        ExternalId logback_classic = externalIdFactory.createMavenExternalId("ch.qos.logback", "logback-classic", "1.2.13");
        ExternalId logback_core = externalIdFactory.createMavenExternalId("ch.qos.logback", "logback-core", "1.2.13");
        ExternalId commons_text = externalIdFactory.createMavenExternalId("org.apache.commons", "commons-text", "1.10.0");
        // SubProjectA dependencies
        ExternalId blackduck_common = externalIdFactory.createMavenExternalId("com.blackduck.integration", "blackduck-common", "67.0.2");
        ExternalId blackduck_common_api = externalIdFactory.createMavenExternalId("com.blackduck.integration", "blackduck-common-api", "2023.4.2.7");
        ExternalId digraph_parser = externalIdFactory.createMavenExternalId("com.paypal.digraph", "digraph-parser", "1.0");
        // SubProjectC dependencies
        ExternalId antlr4_runtime = externalIdFactory.createMavenExternalId("org.antlr", "antlr4-runtime", "4.7.2");

        NameVersionGraphAssert rootGraphAssert = new NameVersionGraphAssert(Forge.MAVEN, root.getDependencyGraph());
        NameVersionGraphAssert subProjectAGraphAssert = new NameVersionGraphAssert(Forge.MAVEN, subProjectA.getDependencyGraph());
        NameVersionGraphAssert subProjectCGraphAssert = new NameVersionGraphAssert(Forge.MAVEN, subProjectC.getDependencyGraph());


        // Root has 2 direct and 1 transitive
        Set<Dependency> rootProjectDirectDependencies = root.getDependencyGraph().getDirectDependencies();
        Assertions.assertEquals(2, rootProjectDirectDependencies.size());
        rootGraphAssert.hasRootDependency(logback_classic);
        rootGraphAssert.hasRootDependency(commons_text);
        rootGraphAssert.hasParentChildRelationship(logback_classic, logback_core);

        // SubProjectA has 2 direct and 1 transitive
        Set<Dependency> subProjectADirectDependencies = subProjectA.getDependencyGraph().getDirectDependencies();
        Assertions.assertEquals(2, subProjectADirectDependencies.size());
        subProjectAGraphAssert.hasRootDependency(blackduck_common);
        subProjectAGraphAssert.hasParentChildRelationship(blackduck_common, blackduck_common_api);
        subProjectAGraphAssert.hasRootDependency(digraph_parser);
        subProjectCGraphAssert.hasRelationshipCount(digraph_parser, 0);

        // Nested SubProjectC has 1 direct and no transitives
        Set<Dependency> subProjectCDirectDependencies = subProjectC.getDependencyGraph().getDirectDependencies();
        Assertions.assertEquals(1, subProjectCDirectDependencies.size());
        subProjectCGraphAssert.hasRootDependency(antlr4_runtime);
        subProjectCGraphAssert.hasRelationshipCount(antlr4_runtime, 0);

        // Empty subProjectB has no dependencies
        Set<Dependency> subProjectBDirectDependencies = subProjectB.getDependencyGraph().getDirectDependencies();
        Assertions.assertEquals(0, subProjectBDirectDependencies.size());
    }
}
