package com.synopsys.integration.detect.battery;

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

import com.synopsys.integration.detect.configuration.DetectProperty;

import freemarker.template.TemplateException;

//This executable types text from a set of files when executed.
public abstract class TypingExecutable extends BatteryExecutable {

    protected TypingExecutable(final DetectProperty detectProperty) {
        super(detectProperty);
    }

    @Override
    public File createExecutable(final int id, final File mockDirectory, final AtomicInteger commandCount) throws IOException, TemplateException {

        //The data file tracks the current invocation count for this exe. It types a different command each invocation.
        //For example GIT types 'url' first, then 'branch' second. This data file contains 0 at first, then 1 after the first run.
        final File dataFile = new File(mockDirectory, "exe-" + id + ".dat");
        FileUtils.writeStringToFile(dataFile, "0", Charset.defaultCharset());

        final Map<String, Object> model = new HashMap<>();
        model.put("dataFile", dataFile.getCanonicalPath());
        model.put("files", Lists.newArrayList(getFilePaths(mockDirectory, commandCount)));
        final File commandFile = new File(mockDirectory, "exe-" + id + ".bat");
        if (SystemUtils.IS_OS_WINDOWS) {
            BatteryFiles.processTemplate("/typing-exe.ftl", commandFile, model, BatteryFiles.UTIL_RESOURCE_PREFIX);
        } else {
            BatteryFiles.processTemplate("/typing-sh.ftl", commandFile, model, BatteryFiles.UTIL_RESOURCE_PREFIX);
        }


        return commandFile;
    }

    public abstract List<String> getFilePaths(File mockDirectory, AtomicInteger commandCount) throws IOException;
}
