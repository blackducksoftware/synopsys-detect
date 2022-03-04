package com.synopsys.integration.detectable.detectables.bitbake;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.bitbake.collect.BitbakeSession;
import com.synopsys.integration.detectable.detectables.bitbake.collect.BuildFileFinder;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeEnvironment;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.model.ShowRecipesResults;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeEnvironmentParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeRecipesParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.LicenseManifestParser;
import com.synopsys.integration.detectable.detectables.bitbake.transform.BitbakeGraphTransformer;
import com.synopsys.integration.detectable.detectables.bitbake.transform.GraphParserTransformer;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
import com.synopsys.integration.exception.IntegrationException;

public class BitbakeExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectableExecutableRunner executableRunner;
    private final GraphParserTransformer graphParserTransformer;
    private final BitbakeGraphTransformer bitbakeGraphTransformer;
    private final BitbakeRecipesParser bitbakeRecipesParser;
    private final ToolVersionLogger toolVersionLogger;
    private final BuildFileFinder buildFileFinder;
    private final LicenseManifestParser licenseManifestParser;
    private final BitbakeEnvironmentParser bitbakeEnvironmentParser;

    public BitbakeExtractor(
        DetectableExecutableRunner executableRunner,
        GraphParserTransformer graphParserTransformer,
        BitbakeGraphTransformer bitbakeGraphTransformer,
        BitbakeRecipesParser bitbakeRecipesParser,
        ToolVersionLogger toolVersionLogger,
        BuildFileFinder buildFileFinder,
        LicenseManifestParser licenseManifestParser,
        BitbakeEnvironmentParser bitbakeEnvironmentParser
    ) {
        this.executableRunner = executableRunner;
        this.graphParserTransformer = graphParserTransformer;
        this.bitbakeGraphTransformer = bitbakeGraphTransformer;
        this.bitbakeRecipesParser = bitbakeRecipesParser;
        this.toolVersionLogger = toolVersionLogger;
        this.buildFileFinder = buildFileFinder;
        this.licenseManifestParser = licenseManifestParser;
        this.bitbakeEnvironmentParser = bitbakeEnvironmentParser;
    }

    public Extraction extract(
        File sourceDirectory,
        File buildEnvScript,
        List<String> sourceArguments,
        List<String> packageNames,
        EnumListFilter<BitbakeDependencyType> dependencyTypeFilter,
        ExecutableTarget bash
    ) throws ExecutableFailedException, IOException {
        List<CodeLocation> codeLocations = new ArrayList<>();
        BitbakeSession bitbakeSession = new BitbakeSession(
            executableRunner,
            bitbakeRecipesParser,
            sourceDirectory,
            buildEnvScript,
            sourceArguments,
            bash,
            toolVersionLogger,
            buildFileFinder,
            bitbakeEnvironmentParser
        );
        bitbakeSession.logBitbakeVersion();
        File buildDir = bitbakeSession.determineBuildDir();
        BitbakeEnvironment bitbakeEnvironment = bitbakeSession.executeBitbakeForEnvironment();
        ShowRecipesResults showRecipesResults = bitbakeSession.executeBitbakeForRecipeLayerCatalog();
        for (String targetImage : packageNames) {
            try {
                codeLocations.add(generateCodeLocationForTargetImage(
                    dependencyTypeFilter,
                    bitbakeSession,
                    buildDir,
                    bitbakeEnvironment,
                    showRecipesResults,
                    targetImage
                ));
            } catch (IOException | IntegrationException | NotImplementedException | ExecutableFailedException e) {
                logger.error(String.format("Failed to extract a Code Location while running Bitbake against package '%s': %s", targetImage, e.getMessage()));
                logger.debug(e.getMessage(), e);
            }
        }
        Extraction extraction;
        if (codeLocations.isEmpty()) {
            extraction = new Extraction.Builder()
                .failure("No Code Locations were generated during extraction")
                .build();

        } else {
            extraction = new Extraction.Builder()
                .success(codeLocations)
                .build();
        }
        return extraction;
    }

    @NotNull
    private CodeLocation generateCodeLocationForTargetImage(
        EnumListFilter<BitbakeDependencyType> dependencyTypeFilter,
        BitbakeSession bitbakeSession,
        File buildDir,
        BitbakeEnvironment bitbakeEnvironment,
        ShowRecipesResults showRecipesResults,
        String packageName
    ) throws IntegrationException, IOException, ExecutableFailedException {
        Map<String, String> imageRecipes = null;
        if (dependencyTypeFilter.shouldExclude(BitbakeDependencyType.BUILD)) {
            imageRecipes = readImageRecipes(buildDir, packageName, bitbakeEnvironment);
        }
        BitbakeGraph bitbakeGraph = generateBitbakeGraph(bitbakeSession, buildDir, packageName, showRecipesResults.getLayerNames());
        DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, showRecipesResults.getRecipesWithLayers(), imageRecipes);
        return new CodeLocation(dependencyGraph);
    }

    private Map<String, String> readImageRecipes(File buildDir, String targetImageName, BitbakeEnvironment bitbakeEnvironment)
        throws IntegrationException, IOException {
        Optional<File> licenseManifestFile = buildFileFinder.findLicenseManifestFile(buildDir, targetImageName, bitbakeEnvironment);
        if (licenseManifestFile.isPresent()) {
            List<String> licenseManifestLines = FileUtils.readLines(licenseManifestFile.get(), StandardCharsets.UTF_8);
            return licenseManifestParser.collectImageRecipes(licenseManifestLines);
        } else {
            logger.info("No license.manifest file found for target image {}; every dependency will be considered a BUILD dependency.", targetImageName);
            return new HashMap<>(0);
        }
    }

    private BitbakeGraph generateBitbakeGraph(
        BitbakeSession bitbakeSession,
        File buildDir,
        String packageName,
        Set<String> knownLayers
    ) throws IOException, IntegrationException, ExecutableFailedException {
        File taskDependsFile = bitbakeSession.executeBitbakeForDependencies(buildDir, packageName);
        if (logger.isTraceEnabled()) {
            logger.trace(FileUtils.readFileToString(taskDependsFile, Charset.defaultCharset()));
        }
        InputStream dependsFileInputStream = FileUtils.openInputStream(taskDependsFile);
        GraphParser graphParser = new GraphParser(dependsFileInputStream);
        return graphParserTransformer.transform(graphParser, knownLayers);
    }
}
