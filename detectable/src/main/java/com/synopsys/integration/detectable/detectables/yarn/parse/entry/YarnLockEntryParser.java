package com.synopsys.integration.detectable.detectables.yarn.parse.entry;

import java.util.List;
import java.util.Optional;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.section.YarnLockEntrySectionParserSet;

public class YarnLockEntryParser {
    private final YarnLockLineAnalyzer yarnLockLineAnalyzer;
    private final YarnLockEntrySectionParserSet yarnLockEntrySectionParserSet;

    public YarnLockEntryParser(YarnLockLineAnalyzer yarnLockLineAnalyzer, YarnLockEntrySectionParserSet yarnLockEntrySectionParserSet) {
        this.yarnLockLineAnalyzer = yarnLockLineAnalyzer;
        this.yarnLockEntrySectionParserSet = yarnLockEntrySectionParserSet;
    }

    public YarnLockEntryParseResult parseNextEntry(List<String> yarnLockFileLines, int entryStartIndex) {
        YarnLockEntryBuilder yarnLockEntryBuilder = new YarnLockEntryBuilder();
        int fileLineIndex = entryStartIndex;
        int entryLineIndex = 0;
        while (fileLineIndex < yarnLockFileLines.size()) {
            String curLine = yarnLockFileLines.get(fileLineIndex);
            if (passedEndOfEntry(entryLineIndex, yarnLockEntryBuilder, curLine)) {
                return createResult(fileLineIndex, yarnLockEntryBuilder);
            }
            // parseSection returns the last line it consumed; parsing resumes on the next line
            fileLineIndex = yarnLockEntrySectionParserSet.parseSection(yarnLockEntryBuilder, yarnLockFileLines, fileLineIndex);
            entryLineIndex++;
            fileLineIndex++;
        }
        Optional<YarnLockEntry> entry = yarnLockEntryBuilder.buildIfValid();
        return new YarnLockEntryParseResult(yarnLockFileLines.size() - 1, entry.orElse(null));
    }

    private YarnLockEntryParseResult createResult(int fileLineIndex, YarnLockEntryBuilder entryBuilder) {
        YarnLockEntry entry = entryBuilder.build();
        return new YarnLockEntryParseResult(fileLineIndex - 1, entry);
    }

    private boolean passedEndOfEntry(int entryLineIndex, YarnLockEntryBuilder builder, String curLine) {
        if ((entryLineIndex == 0) || !builder.valid()) {
            return false;
        }
        // If we've left the indented lines, we're done parsing this entry
        int indentDepth = yarnLockLineAnalyzer.measureIndentDepth(curLine);
        return (indentDepth == 0);
    }
}
