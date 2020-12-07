package com.synopsys.integration.detectable.detectables.conan.functional;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.conan.ConanCodeLocationGenerator;
import com.synopsys.integration.detectable.detectables.conan.ConanDetectableResult;
import com.synopsys.integration.detectable.detectables.conan.lockfile.parser.ConanLockfileParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
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
        System.out.printf("code location name: %s\n", result.getCodeLocation().getExternalId().get().getName());
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
        System.out.printf("code location name: %s\n", result.getCodeLocation().getExternalId().get().getName());
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
        System.out.printf("code location name: %s\n", result.getCodeLocation().getExternalId().get().getName());
    }
}
