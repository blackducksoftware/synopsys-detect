package com.synopsys.integration.detectable.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

public abstract class FileFormatChecker {
    protected final Set<String> knownFileFormats;

    protected FileFormatChecker(String firstKnownFileFormat, String... otherKnownFileFormats) {
        this(createFormats(firstKnownFileFormat, otherKnownFileFormats));
    }

    private static Set<String> createFormats(String firstKnownFileFormat, String... otherKnownFileFormats) {
        Set<String> knownFileFormats = new HashSet<>();
        knownFileFormats.add(firstKnownFileFormat);
        knownFileFormats.addAll(Arrays.asList(otherKnownFileFormats));
        return knownFileFormats;
    }

    protected FileFormatChecker(String[] knownFileFormats) {
        this(Arrays.stream(knownFileFormats).collect(Collectors.toSet()));
    }

    protected FileFormatChecker(Set<String> knownFileFormats) {
        this.knownFileFormats = knownFileFormats;
    }

    public abstract void handleUnknownVersion(@Nullable String unknownFileFormat);

    protected boolean checkForVersionCompatibility(Supplier<@Nullable String> getFileFormatVersion) {
        return checkForVersionCompatibility(getFileFormatVersion.get());
    }

    protected boolean checkForVersionCompatibility(@Nullable String fileFormatVersion) {
        if (fileFormatVersion == null || isVersionUnknown(fileFormatVersion)) {
            handleUnknownVersion(fileFormatVersion);
            return false;
        }
        return true;
    }

    private boolean isVersionUnknown(String fileFormatVersion) {
        return !knownFileFormats.contains(fileFormatVersion);
    }

    public Set<String> getKnownFileFormats() {
        return knownFileFormats;
    }
}
