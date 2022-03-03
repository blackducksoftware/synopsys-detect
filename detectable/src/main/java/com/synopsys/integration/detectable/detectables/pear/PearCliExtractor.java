package com.synopsys.integration.detectable.detectables.pear;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.pear.model.PackageDependency;
import com.synopsys.integration.detectable.detectables.pear.parse.PearListParser;
import com.synopsys.integration.detectable.detectables.pear.parse.PearPackageDependenciesParser;
import com.synopsys.integration.detectable.detectables.pear.parse.PearPackageXmlParser;
import com.synopsys.integration.detectable.detectables.pear.transform.PearDependencyGraphTransformer;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.util.NameVersion;

public class PearCliExtractor {
    private static final String PACKAGE_XML_FILENAME = "package.xml";

    private final ExternalIdFactory externalIdFactory;
    private final DetectableExecutableRunner executableRunner;
    private final PearDependencyGraphTransformer pearDependencyGraphTransformer;
    private final PearPackageXmlParser pearPackageXmlParser;
    private final PearPackageDependenciesParser pearPackageDependenciesParser;
    private final PearListParser pearListParser;

    public PearCliExtractor(
        ExternalIdFactory externalIdFactory,
        DetectableExecutableRunner executableRunner,
        PearDependencyGraphTransformer pearDependencyGraphTransformer,
        PearPackageXmlParser pearPackageXmlParser,
        PearPackageDependenciesParser pearPackageDependenciesParser,
        PearListParser pearListParser
    ) {
        this.externalIdFactory = externalIdFactory;
        this.executableRunner = executableRunner;
        this.pearDependencyGraphTransformer = pearDependencyGraphTransformer;
        this.pearPackageXmlParser = pearPackageXmlParser;
        this.pearPackageDependenciesParser = pearPackageDependenciesParser;
        this.pearListParser = pearListParser;
    }

    public Extraction extract(ExecutableTarget pearExe, File packageXmlFile, File workingDirectory) {
        try {
            ExecutableOutput pearListOutput = executableRunner.execute(ExecutableUtils.createFromTarget(workingDirectory, pearExe, "list"));
            ExecutableOutput packageDependenciesOutput = executableRunner.execute(ExecutableUtils.createFromTarget(
                workingDirectory,
                pearExe,
                "package-dependencies",
                PACKAGE_XML_FILENAME
            ));
            assertValidExecutableOutput(pearListOutput, packageDependenciesOutput);

            Map<String, String> dependencyNameVersionMap = pearListParser.parse(pearListOutput.getStandardOutputAsList());
            List<PackageDependency> packageDependencies = pearPackageDependenciesParser.parse(packageDependenciesOutput.getStandardOutputAsList());
            DependencyGraph dependencyGraph = pearDependencyGraphTransformer.buildDependencyGraph(dependencyNameVersionMap, packageDependencies);

            try (InputStream packageXmlInputStream = new FileInputStream(packageXmlFile)) {
                NameVersion projectNameVersion = pearPackageXmlParser.parse(packageXmlInputStream);

                ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PEAR, projectNameVersion.getName(), projectNameVersion.getVersion());
                CodeLocation detectCodeLocation = new CodeLocation(dependencyGraph, externalId);

                return new Extraction.Builder()
                    .success(detectCodeLocation)
                    .projectName(projectNameVersion.getName())
                    .projectVersion(projectNameVersion.getVersion())
                    .build();
            }
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private void assertValidExecutableOutput(ExecutableOutput pearListing, ExecutableOutput pearDependencies) throws IntegrationException {
        if (pearDependencies.getReturnCode() != 0 || StringUtils.isNotBlank(pearDependencies.getErrorOutput())) {
            throw new IntegrationException("Pear dependencies exit code must be 0 and have no error output.");
        } else if (pearListing.getReturnCode() != 0 || StringUtils.isNotBlank(pearListing.getErrorOutput())) {
            throw new IntegrationException("Pear listing exit code must be 0 and have no error output.");
        }

        if (StringUtils.isBlank(pearDependencies.getStandardOutput()) && StringUtils.isBlank(pearListing.getStandardOutput())) {
            throw new IntegrationException("No information retrieved from running pear commands");
        }
    }
}
