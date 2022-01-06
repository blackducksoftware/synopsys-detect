package com.synopsys.integration.detectable.detectables.bitbake;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeRecipe;
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
    private final FileFinder fileFinder;
    private final GraphParserTransformer graphParserTransformer;
    private final BitbakeGraphTransformer bitbakeGraphTransformer;
    private final BitbakeRecipesParser bitbakeRecipesParser;
    private final BitbakeRecipesToLayerMapConverter bitbakeRecipesToLayerMap;
    private final ToolVersionLogger toolVersionLogger;
    private final LicenseManifestFinder licenseManifestFinder;
    private final LicenseManifestParser licenseManifestParser;

    public BitbakeExtractor(DetectableExecutableRunner executableRunner, FileFinder fileFinder, GraphParserTransformer graphParserTransformer, BitbakeGraphTransformer bitbakeGraphTransformer,
        BitbakeRecipesParser bitbakeRecipesParser, BitbakeRecipesToLayerMapConverter bitbakeRecipesToLayerMap, ToolVersionLogger toolVersionLogger, LicenseManifestFinder licenseManifestFinder,
        LicenseManifestParser licenseManifestParser) {
        this.executableRunner = executableRunner;
        this.fileFinder = fileFinder;
        this.graphParserTransformer = graphParserTransformer;
        this.bitbakeGraphTransformer = bitbakeGraphTransformer;
        this.bitbakeRecipesParser = bitbakeRecipesParser;
        this.bitbakeRecipesToLayerMap = bitbakeRecipesToLayerMap;
        this.toolVersionLogger = toolVersionLogger;
        this.licenseManifestFinder = licenseManifestFinder;
        this.licenseManifestParser = licenseManifestParser;
    }

    public Extraction extract(File sourceDirectory, File buildEnvScript, List<String> sourceArguments, List<String> packageNames, boolean followSymLinks, Integer searchDepth, boolean includeDevDependencies, ExecutableTarget bash) {
        List<CodeLocation> codeLocations = new ArrayList<>();

        BitbakeSession bitbakeSession = new BitbakeSession(fileFinder, executableRunner, bitbakeRecipesParser, sourceDirectory, buildEnvScript, sourceArguments, bash, toolVersionLogger);
        bitbakeSession.logBitbakeVersion();
        for (String packageAndOptionalLicenseFilePath : packageNames) {
            String packageName = extractPackageName(packageAndOptionalLicenseFilePath);
            Optional<String> pathToLicenseManifestFile = extractPathToLicenseManifestFile(packageAndOptionalLicenseFilePath);
            Map<String, String> imageRecipes = null;
            try {
                if (!includeDevDependencies) {
                    imageRecipes = readImageRecipes(sourceDirectory, packageName, pathToLicenseManifestFile.orElse(null));
                }
                BitbakeGraph bitbakeGraph = generateBitbakeGraph(bitbakeSession, sourceDirectory, packageName, followSymLinks, searchDepth);
                List<BitbakeRecipe> bitbakeRecipes = bitbakeSession.executeBitbakeForRecipeLayerCatalog();
                Map<String, String> recipeNameToLayersMap = bitbakeRecipesToLayerMap.convert(bitbakeRecipes);

                DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, recipeNameToLayersMap, imageRecipes, includeDevDependencies);
                CodeLocation codeLocation = new CodeLocation(dependencyGraph);

                codeLocations.add(codeLocation);

            } catch (IOException | IntegrationException | ExecutableRunnerException | NotImplementedException e) {
                logger.error(String.format("Failed to extract a Code Location while running Bitbake against package '%s'", packageName));
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

    private String extractPackageName(String givenPackageName) {
        String packageName;
        if (givenPackageName.contains(":")) {
            int colonIndex = givenPackageName.indexOf(':');
            packageName = givenPackageName.substring(0, colonIndex);
        } else {
            packageName = givenPackageName;
        }
        return packageName;
    }

    private Optional<String> extractPathToLicenseManifestFile(String givenPackageName) {
        String pathToLicenseManifestFile = null;
        if (givenPackageName.contains(":")) {
            int colonIndex = givenPackageName.indexOf(':');
            pathToLicenseManifestFile = givenPackageName.substring(colonIndex+1);
        }
        return Optional.ofNullable(pathToLicenseManifestFile);
    }

    private Map<String, String> readImageRecipes(File sourceDirectory, String targetImageName, @Nullable String pathToLicenseManifestFile) throws IntegrationException, IOException {
        File licenseManifestFile = licenseManifestFinder.find(sourceDirectory, targetImageName, pathToLicenseManifestFile);
        List<String> licenseManifestLines = FileUtils.readLines(licenseManifestFile, StandardCharsets.UTF_8);
        return licenseManifestParser.collectImageRecipes(licenseManifestLines);
    }

    private BitbakeGraph generateBitbakeGraph(BitbakeSession bitbakeSession, File sourceDirectory, String packageName, boolean followSymLinks, Integer searchDepth) throws ExecutableRunnerException, IOException, IntegrationException {
        File taskDependsFile = bitbakeSession.executeBitbakeForDependencies(sourceDirectory, packageName, followSymLinks, searchDepth)
            .orElseThrow(() -> new IntegrationException("Failed to find file \"task-depends.dot\"."));

        logger.trace(FileUtils.readFileToString(taskDependsFile, Charset.defaultCharset()));

        InputStream dependsFileInputStream = FileUtils.openInputStream(taskDependsFile);
        GraphParser graphParser = new GraphParser(dependsFileInputStream);

        return graphParserTransformer.transform(graphParser);
    }
}
