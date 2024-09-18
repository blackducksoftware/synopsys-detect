package com.blackduck.integration.detectable.detectables.clang.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.blackduck.integration.detectable.util.MockDetectableEnvironment;
import com.blackduck.integration.detectable.util.MockFileFinder;
import org.junit.jupiter.api.Test;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.blackduck.integration.detectable.detectables.clang.ClangDetectable;
import com.blackduck.integration.detectable.detectables.clang.ClangDetectableOptions;
import com.blackduck.integration.detectable.detectables.clang.ClangExtractor;
import com.blackduck.integration.detectable.detectables.clang.packagemanager.ClangPackageManager;
import com.blackduck.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerRunner;

public class ClangDetectableTest {
    private static final String JSON_COMPILATION_DATABASE_FILENAME = "compile_commands.json";

    @Test
    public void testApplicable() {
        DetectableExecutableRunner executableRunner = null;
        List<ClangPackageManager> availablePackageManagers = new ArrayList<>(0);
        ClangExtractor clangExtractor = null;
        ClangPackageManagerRunner packageManagerRunner = null;

        ClangDetectableOptions options = new ClangDetectableOptions(false);
        DetectableEnvironment environment = MockDetectableEnvironment.empty();
        FileFinder fileFinder = MockFileFinder.withFileNamed(JSON_COMPILATION_DATABASE_FILENAME);

        ClangDetectable detectable = new ClangDetectable(environment, executableRunner, fileFinder, availablePackageManagers, clangExtractor, options, packageManagerRunner);

        assertTrue(detectable.applicable().getPassed());
    }
}
