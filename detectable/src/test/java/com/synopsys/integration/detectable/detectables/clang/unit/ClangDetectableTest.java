package com.synopsys.integration.detectable.detectables.clang.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.clang.ClangDetectable;
import com.synopsys.integration.detectable.detectables.clang.ClangDetectableOptions;
import com.synopsys.integration.detectable.detectables.clang.ClangExtractor;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManager;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerRunner;

public class ClangDetectableTest {

    @Test
    public void testApplicable() {

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);
        final List<ClangPackageManager> availablePackageManagers = new ArrayList<>(0);
        final ClangExtractor clangExtractor = null;
        final ClangDetectableOptions options = Mockito.mock(ClangDetectableOptions.class);
        final ClangPackageManagerRunner packageManagerRunner = null;

        Mockito.when(fileFinder.findFile(Mockito.any(File.class), Mockito.anyString())).thenReturn(new File("."));
        Mockito.when(environment.getDirectory()).thenReturn(new File("."));

        final ClangDetectable detectable = new ClangDetectable(environment, executableRunner, fileFinder, availablePackageManagers, clangExtractor,
            options, packageManagerRunner);

        assertTrue(detectable.applicable().getPassed());
    }
}
