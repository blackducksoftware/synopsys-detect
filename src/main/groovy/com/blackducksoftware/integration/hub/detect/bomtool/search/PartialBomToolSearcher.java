package com.blackducksoftware.integration.hub.detect.bomtool.search;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public abstract class PartialBomToolSearcher<T extends BomToolSearchResult> implements BomToolSearcher<T> {
    final ExecutableManager executableManager;
    final ExecutableRunner executableRunner;
    final DetectFileManager detectFileManager;
    final DetectConfiguration detectConfiguration;

    public PartialBomToolSearcher(final ExecutableManager executableManager, final ExecutableRunner executableRunner, final DetectFileManager detectFileManager, final DetectConfiguration detectConfiguration) {
        this.executableManager = executableManager;
        this.executableRunner = executableRunner;
        this.detectFileManager = detectFileManager;
        this.detectConfiguration = detectConfiguration;
    }

    @Override
    public final T getBomToolSearchResult(final String directoryPathToSearch) throws BomToolException {
        if (StringUtils.isBlank(directoryPathToSearch)) {
            throw new BomToolException(String.format("The provided path %s is empty.", directoryPathToSearch));
        }

        final File directoryToSearch = new File(directoryPathToSearch);
        return getBomToolSearchResult(directoryToSearch);
    }

    @Override
    public final T getBomToolSearchResult(final File directoryToSearch) throws BomToolException {
        if (directoryToSearch == null || !directoryToSearch.isDirectory()) {
            throw new BomToolException(String.format("The provided file %s is not a directory.", directoryToSearch.getAbsolutePath()));
        }

        return getSearchResult(directoryToSearch);
    }

    public abstract T getSearchResult(File directoryToSearch);

    public String findExecutablePath(final ExecutableType executable, final boolean searchSystemPath, final File directoryToSearch, final String optionalPathOverride) {
        if (StringUtils.isNotBlank(optionalPathOverride)) {
            return optionalPathOverride;
        }

        return executableManager.getExecutablePath(executable, searchSystemPath, directoryToSearch.getAbsolutePath());
    }

}
