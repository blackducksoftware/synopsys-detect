package com.synopsys.integration.detect.battery.util.executable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detect.battery.util.BatteryFiles;

import freemarker.template.TemplateException;

public class ResourceTypingExecutableCreator extends TypingExecutableCreator {
    private final List<String> toType;

    public ResourceTypingExecutableCreator(List<String> toType) {
        this.toType = toType;
    }

    @Override
    public List<String> getFilePaths(BatteryExecutableInfo executableInfo, AtomicInteger commandCount) throws IOException, TemplateException {
        List<String> filePaths = new ArrayList<>();
        for (String resource : toType) {
            File file = BatteryFiles.asFile(resource);
            File commandTextFile = new File(executableInfo.getMockDirectory(), "cmd-" + commandCount.getAndIncrement() + ".txt");

            if (file.getName().endsWith(".ftl")) { //TODO: Share this code with the other resource runners.
                Map<String, String> dataModel = new HashMap<>();
                dataModel.put("sourcePath", executableInfo.getSourceDirectory().getCanonicalPath());
                BatteryFiles.processTemplate(file, commandTextFile, dataModel);
            } else {
                try (InputStream commandText = BatteryFiles.asInputStream(resource)) {
                    Assertions.assertNotNull(commandText, "Unable to find resource: " + resource);
                    Files.copy(commandText, commandTextFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            filePaths.add(commandTextFile.getCanonicalPath());
        }
        return filePaths;
    }
}
