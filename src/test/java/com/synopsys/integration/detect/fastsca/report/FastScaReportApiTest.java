package com.synopsys.integration.detect.fastsca.report;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.fastsca.model.FastScaDependency;
import com.synopsys.integration.detect.fastsca.model.FastScaDependencyTree;
import com.synopsys.integration.detect.fastsca.model.FastScaDependencyType;
import com.synopsys.integration.detect.fastsca.model.FastScaEvidence;
import com.synopsys.integration.detect.fastsca.model.FastScaMetadata;

public class FastScaReportApiTest {
	private FastScaReportApi fastScaReportApi;
	
	private Path tempDirectoryPath;
	
	@BeforeEach
	public void beforeEach() throws IOException {
		// Need to supply valid license key but do not hardcode this and commit to source code.
		String licenseKey = "invalid_license_key";
		
		fastScaReportApi = new FastScaReportApi("https", "kbtest.blackducksoftware.com", 443, licenseKey, "Detect/8.0.0");

		tempDirectoryPath = Files.createTempDirectory(FastScaReportWriterTest.class.getSimpleName());
	}
	
	@AfterEach
	public void afterEach() throws IOException {
        try {
            Files.delete(tempDirectoryPath);
        } catch (DirectoryNotEmptyException e) {
        	File tempDirectoryFile = tempDirectoryPath.toFile();
            FileUtils.deleteDirectory(tempDirectoryFile);
        }
	}
	
	@Test
	@Disabled
	public void testCreate() {
		FastScaDependency dependency1 = new FastScaDependency("maven", "org.apache.logging.log4j:log4j-core:2.20.0");
		FastScaDependency dependency2 = new FastScaDependency("maven", "com.fasterxml.jackson.core:jackson-core:2.14.1");
		FastScaDependency dependency3 = new FastScaDependency("maven", "com.google.guava:guava:31.1-jre");
		FastScaDependency dependency4 = new FastScaDependency("maven", "org.yaml:snakeyaml:1.33");
		FastScaDependency dependency5 = new FastScaDependency("maven", "missing2");
		
		FastScaDependencyTree dependencyTree1 = new FastScaDependencyTree(List.of("foocomponent", "org.apache.logging.log4j:log4j-core:2.20.0"), FastScaDependencyType.DIRECT);
		FastScaDependencyTree dependencyTree2 = new FastScaDependencyTree(List.of("foocomponent", "com.madhu:madhu:1.0", "com.fasterxml.jackson.core:jackson-core:2.14.1"), FastScaDependencyType.TRANSITIVE);
		FastScaDependencyTree dependencyTree3a = new FastScaDependencyTree(List.of("foocomponent", "com.google.guava:guava:31.1-jre"), FastScaDependencyType.DIRECT);
		FastScaDependencyTree dependencyTree3b = new FastScaDependencyTree(List.of("foocomponent", "com.fasterxml.jackson.core:jackson-core:2.14.1", "com.google.guava:guava:31.1-jre"), FastScaDependencyType.TRANSITIVE);
		FastScaDependencyTree dependencyTree4 = new FastScaDependencyTree(List.of("foocomponent", "org.yaml:snakeyaml:1.33"), FastScaDependencyType.DIRECT);
		FastScaDependencyTree dependencyTree5 = new FastScaDependencyTree(List.of("foocomponent", "missing2"), FastScaDependencyType.DIRECT);
		
		Set<FastScaDependencyTree> dependencyTrees1 = Set.of(dependencyTree1);
		Set<FastScaDependencyTree> dependencyTrees2 = Set.of(dependencyTree2);
		Set<FastScaDependencyTree> dependencyTrees3 = Set.of(dependencyTree3a, dependencyTree3b);
		Set<FastScaDependencyTree> dependencyTrees4 = Set.of(dependencyTree4);
		Set<FastScaDependencyTree> dependencyTrees5 = Set.of(dependencyTree5);

		FastScaEvidence evidence1 = new FastScaEvidence(dependency1, dependencyTrees1);
		FastScaEvidence evidence2 = new FastScaEvidence(dependency2, dependencyTrees2);
		FastScaEvidence evidence3 = new FastScaEvidence(dependency3, dependencyTrees3);
		FastScaEvidence evidence4 = new FastScaEvidence(dependency4, dependencyTrees4);
		FastScaEvidence evidence5 = new FastScaEvidence(dependency5, dependencyTrees5);
		
		Set<FastScaEvidence> evidences = Set.of(evidence1, evidence2, evidence3, evidence4, evidence5);
		
		FastScaMetadata meta = new FastScaMetadata(UUID.randomUUID(), "Detect", "8.0.0", OffsetDateTime.now(), "FooProject", "1.0.0");
		
		File tempDirectoryFile = tempDirectoryPath.toFile();
		File destinationFile = new File(tempDirectoryFile, "testCreate.json");
		
		fastScaReportApi.create(evidences, meta, destinationFile);
	}
}
