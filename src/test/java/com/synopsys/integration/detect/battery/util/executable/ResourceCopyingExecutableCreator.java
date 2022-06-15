package com.synopsys.integration.detect.battery.util.executable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detect.battery.util.BatteryFiles;

import freemarker.template.TemplateException;

public class ResourceCopyingExecutableCreator extends BatteryExecutableCreator {
    private final List<String> toCopy;
    private OperatingSystemInfo windowsInfo = null;
    private OperatingSystemInfo linuxInfo = null;

    private static class OperatingSystemInfo {
        final int extractionFolderIndex;
        final String extractionFolderPrefix;

        private OperatingSystemInfo(int extractionFolderIndex, String extractionFolderPrefix) {
            this.extractionFolderIndex = extractionFolderIndex;
            this.extractionFolderPrefix = extractionFolderPrefix;
        }
    }

    public ResourceCopyingExecutableCreator(List<String> toCopy) {
        this.toCopy = toCopy;
    }

    public ResourceCopyingExecutableCreator onAnySystem(int extractionFolderIndex, String extractionFolderPrefix) {
        return onWindows(extractionFolderIndex, extractionFolderPrefix).onLinux(extractionFolderIndex, extractionFolderPrefix);
    }

    public ResourceCopyingExecutableCreator onWindows(int extractionFolderIndex, String extractionFolderPrefix) {
        this.windowsInfo = new OperatingSystemInfo(extractionFolderIndex, extractionFolderPrefix);
        return this;
    }

    public ResourceCopyingExecutableCreator onLinux(int extractionFolderIndex, String extractionFolderPrefix) {
        this.linuxInfo = new OperatingSystemInfo(extractionFolderIndex, extractionFolderPrefix);
        return this;
    }

    //Map of Names to Data file paths.
    private Map<String, String> getFilePaths(BatteryExecutableInfo executableInfo, AtomicInteger commandCount) throws IOException, TemplateException {
        Map<String, String> filePaths = new HashMap<>();
        for (String resource : toCopy) {
            File copyingFolder = BatteryFiles.asFile(resource);
            File[] files = copyingFolder.listFiles();
            Assertions.assertNotNull(
                files,
                "When a resource copying executable is used, it should be provided a resource folder. Verify it is a folder and has at least one file: " + resource
            );
            for (File file : files) {
                File commandTextFile = new File(executableInfo.getMockDirectory(), "data-" + commandCount.getAndIncrement() + ".dat");
                if (file.getName().endsWith(".ftl")) {
                    Map<String, String> dataModel = new HashMap<>();
                    dataModel.put("sourcePath", executableInfo.getSourceDirectory().getCanonicalPath());
                    BatteryFiles.processTemplate(file, commandTextFile, dataModel);
                    filePaths.put(commandTextFile.getCanonicalPath(), FilenameUtils.removeExtension(file.getName()));
                } else {
                    Files.copy(file.toPath(), commandTextFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    filePaths.put(commandTextFile.getCanonicalPath(), file.getName());
                }
            }
        }
        return filePaths;
    }

    @Override
    public File createExecutable(int id, BatteryExecutableInfo executableInfo, AtomicInteger commandCount) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        Assertions.assertNotNull(
            linuxInfo,
            "If you have a resource copying executable, you must specify operating system information for both windows and linux but linux information could not be found."
        );
        Assertions.assertNotNull(
            windowsInfo,
            "If you have a resource copying executable, you must specify operating system information for both windows and linux but windows information could not be found."
        );
        if (SystemUtils.IS_OS_WINDOWS) {
            model.put("extractionFolderIndex", windowsInfo.extractionFolderIndex);
            model.put("extractionFolderPrefix", windowsInfo.extractionFolderPrefix);
        } else {
            model.put("extractionFolderIndex", linuxInfo.extractionFolderIndex);
            model.put("extractionFolderPrefix", linuxInfo.extractionFolderPrefix);
        }
        List<Object> files = new ArrayList<>();
        getFilePaths(executableInfo, commandCount).forEach((key, value) -> {
            Map<String, String> modelEntry = new HashMap<>();
            modelEntry.put("from", key);
            modelEntry.put("to", value);
            files.add(modelEntry);
        });
        model.put("files", files);

        File commandFile;
        if (SystemUtils.IS_OS_WINDOWS) {
            commandFile = new File(executableInfo.getMockDirectory(), "exe-" + id + ".bat");
            BatteryFiles.processTemplate("/copying-exe.ftl", commandFile, model, BatteryFiles.UTIL_RESOURCE_PREFIX);
        } else {
            commandFile = new File(executableInfo.getMockDirectory(), "sh-" + id + ".sh");
            BatteryFiles.processTemplate("/copying-sh.ftl", commandFile, model, BatteryFiles.UTIL_RESOURCE_PREFIX);
            Assertions.assertTrue(commandFile.setExecutable(true));
        }

        return commandFile;
    }
}
