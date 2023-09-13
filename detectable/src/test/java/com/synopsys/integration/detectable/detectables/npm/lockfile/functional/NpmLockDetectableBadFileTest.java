package com.synopsys.integration.detectable.detectables.npm.lockfile.functional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.common.util.finder.SimpleFileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.synopsys.integration.detectable.factory.DetectableFactory;
import com.synopsys.integration.detectable.functional.FunctionalDetectableExecutableRunner;

public class NpmLockDetectableBadFileTest {
    
    @NotNull
    private final String name;

    @NotNull
    private final Path tempDirectory;

    @NotNull
    private final Path sourceDirectory;

    @NotNull
    private final FunctionalDetectableExecutableRunner executableRunner;

    @NotNull
    public final DetectableFactory detectableFactory;

    public NpmLockDetectableBadFileTest() throws IOException {
        this.name = "npmBadLock";

        this.tempDirectory = Files.createTempDirectory(name);
        this.sourceDirectory = tempDirectory.resolve("source");

        this.executableRunner = new FunctionalDetectableExecutableRunner();

        FileFinder fileFinder = new SimpleFileFinder();
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        this.detectableFactory = new DetectableFactory(fileFinder, executableRunner, externalIdFactory, gson);
    }

    public void setup() throws IOException {
        List<String> fileContent = Arrays.asList(
            "{",
            "   \"name\": \"knockout-tournament\",",
            "   \"version\": \"1.0.0\",",
            "   \"lockfileVersion\": 1,",
            "   \"requires\": true,",
            "   \"dependencies\": {",
            "       \"balanced-match\": {",
            "           \"version\": \"1.0.0\",",
            "           \"resolved\": \"https://registry.npmjs.org/balanced-match/-/balanced-match-1.0.0.tgz\",",
            "           \"integrity\": \"sha1-ibTRmasr7kneFk6gK4nORi1xt2c=\",",
            "           \"dev\": true",
            "       }",
            "   }",
            "}"
        );
        
        Path relativePath = sourceDirectory.resolve(Paths.get("package-lock.json"));
        Files.createDirectories(relativePath.getParent());
        Files.write(relativePath, fileContent);
    }

    @Test
    public void testNpmLockDetectableBadFile() throws IOException, DetectableException {
        setup();
        
        DetectableEnvironment detectableEnvironment = new DetectableEnvironment(sourceDirectory.toFile());
        Detectable detectable = detectableFactory.createNpmPackageLockDetectable(detectableEnvironment, new NpmLockfileOptions(EnumListFilter.excludeNone()));
        
        detectable.applicable();
        DetectableResult extractable = detectable.extractable();
        Assertions.assertEquals("No packages object was found. This may be due to a package-lock.json or npm-shrinkwrap.json file created by an earlier version of npm. Please run 'npm install' with a supported version of npm and try again.", extractable.toDescription()); 
        FileUtils.deleteDirectory(tempDirectory.toFile());
    }
}
