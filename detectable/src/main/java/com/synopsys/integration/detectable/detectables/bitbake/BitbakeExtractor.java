package com.synopsys.integration.detectable.detectables.bitbake;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.util.ExcludedDependencyTypeFilter;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeEnvironment;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeRecipe;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeEnvironmentParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeGraphTransformer;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeRecipesParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.GraphParserTransformer;
import com.synopsys.integration.detectable.detectables.bitbake.parse.LicenseManifestParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class BitbakeExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectableExecutableRunner executableRunner;
    private final GraphParserTransformer graphParserTransformer;
    private final BitbakeGraphTransformer bitbakeGraphTransformer;
    private final BitbakeRecipesParser bitbakeRecipesParser;
    private final BitbakeRecipesToLayerMapConverter bitbakeRecipesToLayerMap;
    private final ToolVersionLogger toolVersionLogger;
    private final BuildFileFinder buildFileFinder;
    private final LicenseManifestParser licenseManifestParser;
    private final BitbakeEnvironmentParser bitbakeEnvironmentParser;

    public BitbakeExtractor(DetectableExecutableRunner executableRunner, GraphParserTransformer graphParserTransformer, BitbakeGraphTransformer bitbakeGraphTransformer,
        BitbakeRecipesParser bitbakeRecipesParser, BitbakeRecipesToLayerMapConverter bitbakeRecipesToLayerMap, ToolVersionLogger toolVersionLogger, BuildFileFinder buildFileFinder,
        LicenseManifestParser licenseManifestParser, BitbakeEnvironmentParser bitbakeEnvironmentParser) {
        this.executableRunner = executableRunner;
        this.graphParserTransformer = graphParserTransformer;
        this.bitbakeGraphTransformer = bitbakeGraphTransformer;
        this.bitbakeRecipesParser = bitbakeRecipesParser;
        this.bitbakeRecipesToLayerMap = bitbakeRecipesToLayerMap;
        this.toolVersionLogger = toolVersionLogger;
        this.buildFileFinder = buildFileFinder;
        this.licenseManifestParser = licenseManifestParser;
        this.bitbakeEnvironmentParser = bitbakeEnvironmentParser;
    }

    public Extraction extract(File sourceDirectory, File buildEnvScript, List<String> sourceArguments, List<String> packageNames, boolean followSymLinks, Integer searchDepth, ExcludedDependencyTypeFilter<BitbakeDependencyType> excludedDependencyTypeFilter, ExecutableTarget bash) {
        List<CodeLocation> codeLocations = new ArrayList<>();

        BitbakeSession bitbakeSession = new BitbakeSession(executableRunner, bitbakeRecipesParser, sourceDirectory, buildEnvScript, sourceArguments, bash, toolVersionLogger, buildFileFinder, bitbakeEnvironmentParser);
        bitbakeSession.logBitbakeVersion();
        File buildDir = bitbakeSession.determineBuildDir();
        BitbakeEnvironment bitbakeEnvironment = bitbakeSession.executeBitbakeForEnvironment();
        for (String packageName : packageNames) {
            Map<String, String> imageRecipes = null;
            try {
                if (excludedDependencyTypeFilter.shouldExcludeDependencyType(BitbakeDependencyType.BUILD)) {
                    imageRecipes = readImageRecipes(buildDir, packageName, bitbakeEnvironment, followSymLinks, searchDepth);
                }
                BitbakeGraph bitbakeGraph = generateBitbakeGraph(bitbakeSession, buildDir, packageName, followSymLinks, searchDepth);
                List<BitbakeRecipe> bitbakeRecipes = bitbakeSession.executeBitbakeForRecipeLayerCatalog();
                Map<String, String> recipeNameToLayersMap = bitbakeRecipesToLayerMap.convert(bitbakeRecipes);

                DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, recipeNameToLayersMap, imageRecipes, excludedDependencyTypeFilter);
                CodeLocation codeLocation = new CodeLocation(dependencyGraph);

                codeLocations.add(codeLocation);

            } catch (IOException | IntegrationException | ExecutableRunnerException | NotImplementedException | ExecutableFailedException e) {
                logger.error(String.format("Failed to extract a Code Location while running Bitbake against package '%s': %s", packageName, e.getMessage()));
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

    private Map<String, String> readImageRecipes(File buildDir, String targetImageName, BitbakeEnvironment bitbakeEnvironment, boolean followSymLinks, int searchDepth) throws IntegrationException, IOException {
        File licenseManifestFile = buildFileFinder.findLicenseManifestFile(buildDir, targetImageName, bitbakeEnvironment, followSymLinks, searchDepth);
        List<String> licenseManifestLines = FileUtils.readLines(licenseManifestFile, StandardCharsets.UTF_8);
        return licenseManifestParser.collectImageRecipes(licenseManifestLines);
    }

    private BitbakeGraph generateBitbakeGraph(BitbakeSession bitbakeSession, File buildDir, String packageName, boolean followSymLinks, Integer searchDepth)
        throws ExecutableRunnerException, IOException, IntegrationException, ExecutableFailedException {
        File taskDependsFile = bitbakeSession.executeBitbakeForDependencies(buildDir, packageName, followSymLinks, searchDepth);
        if (logger.isTraceEnabled()) {
            logger.trace(FileUtils.readFileToString(taskDependsFile, Charset.defaultCharset()));
        }
        InputStream dependsFileInputStream = FileUtils.openInputStream(taskDependsFile);
        GraphParser graphParser = new GraphParser(dependsFileInputStream);
        return graphParserTransformer.transform(graphParser);
    }
}
