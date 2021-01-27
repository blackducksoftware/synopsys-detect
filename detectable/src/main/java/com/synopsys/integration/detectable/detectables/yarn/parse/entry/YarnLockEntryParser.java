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
        int entryLineCount = 0;
        for (int lineIndex = nodeStartIndex + 1; lineIndex < yarnLockFileLines.size(); lineIndex++) {
            String nodeBodyLine = yarnLockFileLines.get(lineIndex);
            logger.trace("Parsing line: {}: {}", lineIndex + 1, nodeBodyLine);
            // Check to see if we've overshot the end of the node
            Optional<YarnLockEntryParseResult> result = getResultIfDone(nodeBodyLine, lineIndex, nodeStartIndex, entryLineCount, yarnLockEntryBuilder);
            if (result.isPresent()) {
                return result.get();
            }
            entryLineCount++;
            // parseElement tells this code what line to parse next (= where it left off)
            lineIndex = yarnLockEntryElementParser.parseElement(yarnLockEntryBuilder, yarnLockFileLines, lineIndex);
        }
        logger.trace("Reached end of conan info output");
        Optional<YarnLockEntry> entry = yarnLockEntryBuilder.build();
        return new YarnLockEntryParseResult(yarnLockFileLines.size() - 1, entry.orElse(null));
    }

    private Optional<YarnLockEntryParseResult> getResultIfDone(String nodeBodyLine, int lineIndex, int entryStartIndex, int bodyLineCount, YarnLockEntryBuilder entryBuilder) {
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
