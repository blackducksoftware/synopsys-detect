package com.synopsys.integration.detect.battery;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;

import com.synopsys.integration.detect.configuration.DetectProperty;

//This executable types text from a set of files when executed.
public abstract class TypingExecutable extends BatteryExecutable {

    protected TypingExecutable(final DetectProperty detectProperty) {
        super(detectProperty);
    }

    @Override
    public File createExecutable(final int id, final File mockDirectory, final AtomicInteger commandCount) throws IOException {
        final File dataFile = new File(mockDirectory, "exe-" + id + ".dat");
        FileUtils.writeStringToFile(dataFile, "0", Charset.defaultCharset());

        String proxyCommand = "@echo off\r\nsetlocal enabledelayedexpansion\r\n";
        proxyCommand += "for /f %%x in (" + dataFile.getCanonicalPath() + ") do (\r\n";
        proxyCommand += "set /a var=%%x\r\n";
        proxyCommand += ")\r\n";
        proxyCommand += "set /a out=%var%+1\r\n";
        proxyCommand += ">" + dataFile.getCanonicalPath() + " echo %out%\r\n";
        int cnt = 0;
        for (final String fileName : getFilePaths(mockDirectory, commandCount)) {
            proxyCommand += "set cmd[" + cnt + "]=\"" + fileName + "\"\r\n";
            cnt++;
        }
        proxyCommand += "type !cmd[%var%]!\r\n";

        final File commandFile = new File(mockDirectory, "exe-" + id + ".bat");
        FileUtils.writeStringToFile(commandFile, proxyCommand, Charset.defaultCharset());

        return commandFile;
    }

    public abstract List<String> getFilePaths(File mockDirectory, AtomicInteger commandCount) throws IOException;
}
