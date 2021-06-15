package com.synopsys.integration.detect.battery.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.executable.ExecutableRunnerException;

import freemarker.template.TemplateException;

public abstract class BatteryTest {
    protected final BatteryContext batteryContext;
    private boolean shouldAssertBdio = false;

    public BatteryTest(String name) {
        this.batteryContext = new BatteryContext(name);
    }

    public BatteryTest(String testName, String resourcePrefix) {
        this.batteryContext = new BatteryContext(testName, resourcePrefix);
    }

    protected abstract List<String> generateArguments() throws IOException;

    public DetectOutput run() {
        DetectOutput detectOutput = null;
        try {
            List<String> allArguments = new ArrayList<>();
            allArguments.addAll(batteryContext.initialize());
            allArguments.addAll(generateArguments());
            detectOutput = new BatteryDetectRunner(batteryContext.getOutputDirectory(), batteryContext.getScriptDirectory(), "").runDetect(allArguments, false);
            if (shouldAssertBdio) {
                new BatteryBdioAssert(batteryContext.getTestName(), batteryContext.getResourcePrefix()).assertBdio(batteryContext.getBdioDirectory());
            }
        } catch (ExecutableRunnerException | JSONException | BdioCompare.BdioCompareException | IOException | TemplateException e) {
            Assertions.assertNull(e, "An exception should not have been thrown!");
        } finally {
            batteryContext.checkAndCleanupBatteryDirectory();
        }

        Assertions.assertNotNull(detectOutput, "");

        return detectOutput;
    }

    public void expectBdioResources() {
        this.shouldAssertBdio = true;
    }

    //Convenience methods for the context:
    public void executableFromResourceFiles(Property detectProperty, String... resourceFiles) {
        batteryContext.executableFromResourceFiles(detectProperty, resourceFiles);
    }

    public void executableSourceFileFromResourceFiles(String windowsName, String linuxName, String... resourceFiles) {
        batteryContext.executableSourceFileFromResourceFiles(windowsName, linuxName, resourceFiles);
    }

    public ResourceCopyingExecutableCreator executableThatCopiesFiles(Property detectProperty, String... resourceFiles) {
        return batteryContext.executableThatCopiesFiles(detectProperty, resourceFiles);
    }

    public ResourceCopyingExecutableCreator executableSourceFileThatCopiesFiles(String windowsName, String linuxName, String... resourceFiles) {
        return batteryContext.executableSourceFileThatCopiesFiles(windowsName, linuxName, resourceFiles);
    }

    public void executable(Property detectProperty, String... responses) {
        batteryContext.executable(detectProperty, responses);
    }

    public void git(String origin, String branch) {
        batteryContext.git(origin, branch);
    }

    public void sourceDirectoryNamed(String name) {
        batteryContext.sourceDirectoryNamed(name);
    }

    public void sourceFileNamed(String filename) {
        batteryContext.sourceFileNamed(filename);
    }

    public void addDirectlyToSourceFolderFromExpandedResource(String filename) {
        batteryContext.addDirectlyToSourceFolderFromExpandedResource(filename);
    }

    public void sourceFolderFromExpandedResource(String filename) {
        batteryContext.sourceFolderFromExpandedResource(filename);
    }

    public void sourceFileFromResource(String filename) {
        batteryContext.sourceFileFromResource(filename);
    }
}
