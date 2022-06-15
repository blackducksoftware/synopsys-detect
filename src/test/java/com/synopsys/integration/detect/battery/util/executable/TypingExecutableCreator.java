package com.synopsys.integration.detect.battery.util.executable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detect.battery.util.BatteryFiles;

import freemarker.template.TemplateException;

//This executable types text from a set of files when executed.
public abstract class TypingExecutableCreator extends BatteryExecutableCreator {

    @Override
    public File createExecutable(int id, BatteryExecutableInfo executableInfo, AtomicInteger commandCount) throws IOException, TemplateException {

        //The data file tracks the current invocation count for this exe. It types a different command each invocation.
        //For example GIT types 'url' first, then 'branch' second. This data file contains 0 at first, then 1 after the first run.
        File dataFile = new File(executableInfo.getMockDirectory(), "exe-" + id + ".dat");
        FileUtils.writeStringToFile(dataFile, "0", Charset.defaultCharset());

        Map<String, Object> model = new HashMap<>();
        model.put("dataFile", dataFile.getCanonicalPath());
        model.put("files", Lists.newArrayList(getFilePaths(executableInfo, commandCount)));
        File commandFile;
        if (SystemUtils.IS_OS_WINDOWS) {
            commandFile = new File(executableInfo.getMockDirectory(), "exe-" + id + ".bat");
            BatteryFiles.processTemplate("/typing-exe.ftl", commandFile, model, BatteryFiles.UTIL_RESOURCE_PREFIX);
        } else {
            commandFile = new File(executableInfo.getMockDirectory(), "sh-" + id + ".sh");
            BatteryFiles.processTemplate("/typing-sh.ftl", commandFile, model, BatteryFiles.UTIL_RESOURCE_PREFIX);
            Assertions.assertTrue(commandFile.setExecutable(true));
        }

        return commandFile;
    }

    public abstract List<String> getFilePaths(BatteryExecutableInfo executableInfo, AtomicInteger commandCount) throws IOException, TemplateException;
}
