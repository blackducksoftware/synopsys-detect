package com.synopsys.integration.detect.battery;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detect.configuration.DetectProperty;

import freemarker.template.TemplateException;

public class ResourceCopyingExecutable extends BatteryExecutable {
    private final List<String> toCopy;
    private final int extractionFolderIndex;
    private final String extractionFolderPrefix;

    protected ResourceCopyingExecutable(final DetectProperty detectProperty, final List<String> toCopy, final int extractionFolderIndex, final String extractionFolderPrefix) {
        super(detectProperty);
        this.toCopy = toCopy;
        this.extractionFolderIndex = extractionFolderIndex;
        this.extractionFolderPrefix = extractionFolderPrefix;
    }

    //Map of Names to Data file paths.
    public Map<String, String> getFilePaths(final File mockDirectory, final AtomicInteger commandCount) throws IOException {
        final Map<String, String> filePaths = new HashMap<>();
        for (final String resource : toCopy) {
            final File copyingFolder = BatteryFiles.asFile(resource);
            final File[] files = copyingFolder.listFiles();
            Assertions.assertNotNull(files, "Unable to find resources: " + resource);
            for (final File file : files) {
                final File commandTextFile = new File(mockDirectory, "data-" + commandCount.getAndIncrement() + ".dat");
                Files.copy(file.toPath(), commandTextFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                filePaths.put(commandTextFile.getCanonicalPath(), file.getName());
            }
        }
        return filePaths;
    }

    @Override
    public File createExecutable(final int id, final File mockDirectory, final AtomicInteger commandCount) throws IOException, TemplateException {

        final Map<String, Object> model = new HashMap<>();
        model.put("extractionFolderIndex", extractionFolderIndex);
        model.put("extractionFolderPrefix", extractionFolderPrefix);
        final List<Object> files = new ArrayList<>();
        getFilePaths(mockDirectory, commandCount).forEach((key, value) -> {
            final Map<String, String> modelEntry = new HashMap<>();
            modelEntry.put("from", key);
            modelEntry.put("to", value);
            files.add(modelEntry);
        });
        model.put("files", files);

        final File commandFile;
        if (SystemUtils.IS_OS_WINDOWS) {
            commandFile = new File(mockDirectory, "exe-" + id + ".bat");
            BatteryFiles.processTemplate("/copying-exe.ftl", commandFile, model, BatteryFiles.UTIL_RESOURCE_PREFIX);
        } else {
            commandFile = new File(mockDirectory, "sh-" + id + ".sh");
            BatteryFiles.processTemplate("/copying-sh.ftl", commandFile, model, BatteryFiles.UTIL_RESOURCE_PREFIX);
            commandFile.setExecutable(true);
        }

        return commandFile;
    }
}
