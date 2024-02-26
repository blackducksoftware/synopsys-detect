package com.synopsys.integration.detectable.detectables.yarn.parse;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;

public class YarnLock {
    @Nullable
    private final String fileFormatVersion;
    private final boolean yarn1Project;
    private final List<YarnLockEntry> entries;

    public YarnLock(@Nullable String fileFormatVersion, boolean yarn1Project, List<YarnLockEntry> entries) {
        this.fileFormatVersion = fileFormatVersion;
        this.yarn1Project = yarn1Project;
        this.entries = entries;
    }

    public Optional<String> getFileFormatVersion() {
        return Optional.ofNullable(fileFormatVersion);
    }

    public boolean isYarn1Project() {
        return yarn1Project;
    }

    public List<YarnLockEntry> getEntries() {
        return entries;
    }
}
