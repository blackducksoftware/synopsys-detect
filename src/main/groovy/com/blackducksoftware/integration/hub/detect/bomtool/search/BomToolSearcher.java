package com.blackducksoftware.integration.hub.detect.bomtool.search;

import com.blackducksoftware.integration.hub.detect.exception.BomToolException;

import java.io.File;

/**
 * Implementors should search the single directory for indicators of the bom tool.
 */
public interface BomToolSearcher<T extends BomToolSearchResult> {
    /**
     * @return true if enough indicators were found in the provided directory for the bom tool to apply, false otherwise.
     * @throws BomToolException if directoryToSearch is not a directory.
     */
    T getBomToolSearchResult(File directoryToSearch) throws BomToolException;

    /**
     * @return true if enough indicators were found in the provided directory for the bom tool to apply, false otherwise
     * @throws BomToolException if directoryToSearch is not a directory.
     */
    T getBomToolSearchResult(String directoryPathToSearch) throws BomToolException;

}
