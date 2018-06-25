package com.blackducksoftware.integration.hub.detect.bomtool.go.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.google.gson.Gson;

public class DepPackager {
    private final Logger logger = LoggerFactory.getLogger(DepPackager.class);

    @Autowired
    ExecutableRunner executableRunner;

    @Autowired
    Gson gson;

    @Autowired
    DetectConfiguration detectConfiguration;

    @Autowired
    ExternalIdFactory externalIdFactory;

    public DependencyGraph makeDependencyGraph(final String sourcePath, final String goDepExecutable) throws IOException {
        final GopkgLockParser gopkgLockParser = new GopkgLockParser(externalIdFactory);
        final String goDepContents = getGopkgLockContents(new File(sourcePath), goDepExecutable);
        if (StringUtils.isNotBlank(goDepContents)) {
            return gopkgLockParser.parseDepLock(goDepContents);
        }
        return null;
    }

    private String getGopkgLockContents(final File file, final String goDepExecutable) throws IOException {
        String gopkgLockContents = null;

        final File gopkgLockFile = new File(file, "Gopkg.lock");
        if (gopkgLockFile.exists()) {
            try (FileInputStream fis = new FileInputStream(gopkgLockFile)) {
                gopkgLockContents = IOUtils.toString(fis, Charset.defaultCharset());
            } catch (final Exception e) {
                gopkgLockContents = null;
            }
            return gopkgLockContents;
        }

        // by default, we won't run 'init' and 'ensure' anymore so just return an empty string
        if (!detectConfiguration.getGoRunDepInit()) {
            logger.info("Skipping Dep commands 'init' and 'ensure'");
            return "";
        }

        final File gopkgTomlFile = new File(file, "Gopkg.toml");
        final File vendorDirectory = new File(file, "vendor");
        final boolean vendorDirectoryExistedBefore = vendorDirectory.exists();
        final File vendorDirectoryBackup = new File(file, "vendor_old");
        if (vendorDirectoryExistedBefore) {
            logger.info(String.format("Backing up %s to %s", vendorDirectory.getAbsolutePath(), vendorDirectoryBackup.getAbsolutePath()));
            FileUtils.moveDirectory(vendorDirectory, vendorDirectoryBackup);
        }

        final String goDepInitString = String.format("%s 'init' on path %s", goDepExecutable, file.getAbsolutePath());
        try {
            logger.info("Running " + goDepInitString);
            final Executable executable = new Executable(file, goDepExecutable, Arrays.asList("init"));
            executableRunner.execute(executable);
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Failed to run %s: %s", goDepInitString, e.getMessage()));
        }

        final String goDepEnsureUpdateString = String.format("%s 'ensure -update' on path %s", goDepExecutable, file.getAbsolutePath());
        try {
            logger.info("Running " + goDepEnsureUpdateString);
            final Executable executable = new Executable(file, goDepExecutable, Arrays.asList("ensure", "-update"));
            executableRunner.execute(executable);
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Failed to run %s: %s", goDepEnsureUpdateString, e.getMessage()));
        }

        if (gopkgLockFile.exists()) {
            try (FileInputStream fis = new FileInputStream(gopkgLockFile)) {
                gopkgLockContents = IOUtils.toString(fis, Charset.defaultCharset());
            } catch (final Exception e) {
                gopkgLockContents = null;
            }
            gopkgLockFile.delete();
            gopkgTomlFile.delete();
            FileUtils.deleteDirectory(vendorDirectory);
            if (vendorDirectoryExistedBefore) {
                logger.info("Restoring back up ${vendorDirectory.getAbsolutePath()} from ${vendorDirectoryBackup.getAbsolutePath()}");
                FileUtils.moveDirectory(vendorDirectoryBackup, vendorDirectory);
            }
        }

        return gopkgLockContents;
    }
}
