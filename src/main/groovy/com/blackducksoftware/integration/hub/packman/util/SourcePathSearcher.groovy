package com.blackducksoftware.integration.hub.packman.util

import org.apache.commons.lang3.math.NumberUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.packman.PackmanProperties

@Component
class SourcePathSearcher {
    @Autowired
    PackmanProperties packmanProperties

    @Autowired
    FileFinder fileFinder

    /**
     * Across all provided source paths, find the subset of source paths that
     * include the provided pattern. You would use the filenamePattern
     * 'pom.xml' to find all maven source paths.
     */
    List<String> findSourcePathsContainingFilenamePattern(String filenamePattern) {
        List<String> matchingSourcePaths = []
        for (String sourcePath : packmanProperties.getSourcePaths()) {
            if (fileFinder.containsAllFiles(sourcePath, filenamePattern)) {
                matchingSourcePaths.add(sourcePath)
            }
        }

        matchingSourcePaths
    }

    /**
     * Across all provided source paths, find the subset of source paths that
     * include the provided pattern, within it and its sub-directories. You would use the filenamePattern
     * 'pom.xml' to find all maven source paths.
     */
    List<String> findSourcePathsContainingFilenamePatternWithDepth(String filenamePattern) {
        List<String> matchingSourcePaths = []
        for (String sourcePath : packmanProperties.getSourcePaths()) {
            if (fileFinder.containsAllFilesWithDepth(sourcePath, NumberUtils.toInt(packmanProperties.getSearchDepth()), filenamePattern)) {
                matchingSourcePaths.add(sourcePath)
            }
        }

        matchingSourcePaths
    }
}
