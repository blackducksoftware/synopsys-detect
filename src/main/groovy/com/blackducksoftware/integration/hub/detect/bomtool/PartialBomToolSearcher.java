package com.blackducksoftware.integration.hub.detect.bomtool;

import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public abstract class PartialBomToolSearcher<T extends BomToolSearchResult> implements BomToolSearcher<T> {
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
        if (!directoryToSearch.isDirectory()) {
            throw new BomToolException(String.format("The provided file %s is not a directory.", directoryToSearch.getAbsolutePath()));
        }

        return isApplicable(directoryToSearch);
    }

    public abstract T getSearchResult(File directoryToSearch);

}
