package com.synopsys.integration.detect.battery.util.executable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;

public class StringTypingExecutableCreator extends TypingExecutableCreator {
    private final List<String> toType;

    public StringTypingExecutableCreator(List<String> toType) {
        this.toType = toType;
    }

    @Override
    public List<String> getFilePaths(BatteryExecutableInfo executableInfo, AtomicInteger commandCount) throws IOException {
        List<String> filePaths = new ArrayList<>();
        for (String text : toType) {
            File commandTextFile = new File(executableInfo.getMockDirectory(), "cmd-" + commandCount.getAndIncrement() + ".txt");
            FileUtils.writeStringToFile(commandTextFile, text, Charset.defaultCharset());
            filePaths.add(commandTextFile.getCanonicalPath());
        }
        return filePaths;
    }
}
