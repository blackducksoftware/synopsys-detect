package com.synopsys.integration.detectable.detectable.file.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.synopsys.integration.detectable.detectable.file.FileFinder;

public class SimpleFileFinder implements FileFinder {

    @Override
    @Nullable
    public File findFile(final File sourceDirectory, final String filenamePattern) {
        return findFiles(sourceDirectory, filenamePattern).stream().findFirst().orElse(null);
    }

    @NotNull
    public List<File> findFiles(final File sourceDirectory, final String filenamePattern) {
        FilenameFilter filter = new WildcardFileFilter(filenamePattern);
        final File[] foundFiles = sourceDirectory.listFiles(filter);

        if (foundFiles == null || foundFiles.length == 0) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(foundFiles);
        }
    }
}
