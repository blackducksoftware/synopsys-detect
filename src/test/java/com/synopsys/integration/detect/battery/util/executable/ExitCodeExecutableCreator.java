package com.synopsys.integration.detect.battery.util.executable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detect.battery.util.BatteryFiles;

import freemarker.template.TemplateException;

//This executable types text from a set of files when executed.
public class ExitCodeExecutableCreator extends BatteryExecutableCreator {
    private final String exitCode;

    public ExitCodeExecutableCreator(String exitCode) {
        this.exitCode = exitCode;
    }

    @Override
    public File createExecutable(int id, BatteryExecutableInfo executableInfo, AtomicInteger commandCount) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put("exitCode", exitCode);
        File commandFile;
        if (SystemUtils.IS_OS_WINDOWS) {
            commandFile = new File(executableInfo.getMockDirectory(), "exe-" + id + ".bat");
            BatteryFiles.processTemplate("/exit-code-exe.ftl", commandFile, model, BatteryFiles.UTIL_RESOURCE_PREFIX);
        } else {
            commandFile = new File(executableInfo.getMockDirectory(), "sh-" + id + ".sh");
            BatteryFiles.processTemplate("/exit-code.ftl", commandFile, model, BatteryFiles.UTIL_RESOURCE_PREFIX);
            Assertions.assertTrue(commandFile.setExecutable(true));
        }

        return commandFile;
    }
}
