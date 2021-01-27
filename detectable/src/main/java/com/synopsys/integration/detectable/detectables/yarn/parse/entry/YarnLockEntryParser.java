package com.synopsys.integration.detectable.detectables.yarn.parse.entry;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.element.YarnLockEntryElementParser;

public class YarnLockEntryParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YarnLockLineAnalyzer yarnLockLineAnalyzer;
    private final YarnLockEntryElementParser yarnLockEntryElementParser;

    public YarnLockEntryParser(YarnLockLineAnalyzer yarnLockLineAnalyzer, YarnLockEntryElementParser yarnLockEntryElementParser) {
        this.yarnLockLineAnalyzer = yarnLockLineAnalyzer;
        this.yarnLockEntryElementParser = yarnLockEntryElementParser;
    }

    public YarnLockEntryParseResult parseEntry(List<String> yarnLockFileLines, int nodeStartIndex) {
        YarnLockEntryBuilder yarnLockEntryBuilder = new YarnLockEntryBuilder();
        int entryLineIndex = 0;
        for (int fileLineIndex = nodeStartIndex; fileLineIndex < yarnLockFileLines.size(); fileLineIndex++) {
            String nodeBodyLine = yarnLockFileLines.get(fileLineIndex);
            logger.trace("Parsing line: {}: {}", fileLineIndex + 1, nodeBodyLine);
            // Check to see if we've overshot the end of the node
            Optional<YarnLockEntryParseResult> result = getResultIfDone(entryLineIndex, nodeBodyLine, fileLineIndex, nodeStartIndex, entryLineIndex, yarnLockEntryBuilder);
            if (result.isPresent()) {
                return result.get();
            }
            // parseElement tells this code what line to parse next (= where it left off)
            fileLineIndex = yarnLockEntryElementParser.parseElement(yarnLockEntryBuilder, yarnLockFileLines, fileLineIndex);
            entryLineIndex++;
        }
        logger.trace("Reached end of yarn lock entry");
        Optional<YarnLockEntry> entry = yarnLockEntryBuilder.build();
        return new YarnLockEntryParseResult(yarnLockFileLines.size() - 1, entry.orElse(null));
    }

    private Optional<YarnLockEntryParseResult> getResultIfDone(int entryLineIndex, String nodeBodyLine, int lineIndex, int entryStartIndex, int bodyLineCount, YarnLockEntryBuilder entryBuilder) {
        if (entryLineIndex == 0) {
            // we're still on the first line, so can't be done yet
            return Optional.empty();
        }
        int indentDepth = yarnLockLineAnalyzer.measureIndentDepth(nodeBodyLine);
        if (indentDepth > 0) {
            // We're not done parsing this node
            return Optional.empty();
        }
        if (bodyLineCount == 0) {
            logger.trace("This wasn't a node");
            return Optional.of(new YarnLockEntryParseResult(entryStartIndex));
        } else {
            logger.trace("Reached end of node");
            Optional<YarnLockEntry> node = entryBuilder.build();
            return Optional.of(new YarnLockEntryParseResult(lineIndex - 1, node.orElse(null)));
        }
    }
}
