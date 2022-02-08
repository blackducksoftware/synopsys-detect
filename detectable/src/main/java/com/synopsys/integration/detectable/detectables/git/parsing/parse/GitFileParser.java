package com.synopsys.integration.detectable.detectables.git.parsing.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigNode;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class GitFileParser {
    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));

    public String parseGitHead(String headFileContent) {
        return headFileContent.trim().replaceFirst("ref:\\w*", "").trim();
    }

    public List<GitConfigNode> parseGitConfig(List<String> gitConfigLines) {
        List<GitConfigNode> gitConfigNodes = new ArrayList<>();
        List<String> lineBuffer = new ArrayList<>();
        for (String rawLine : gitConfigLines) {
            String line = StringUtils.stripToEmpty(rawLine);

            if (StringUtils.isBlank(line)) {
                continue;
            }

            if (isGitConfigNodeStart(line)) {
                Optional<GitConfigNode> gitConfigNode = processGitConfigNodeLines(lineBuffer);
                gitConfigNode.ifPresent(gitConfigNodes::add);
                lineBuffer.clear();
            }

            lineBuffer.add(line);
        }

        processGitConfigNodeLines(lineBuffer).ifPresent(gitConfigNodes::add);

        return gitConfigNodes;
    }

    private boolean isGitConfigNodeStart(String line) {
        return line.startsWith("[") && line.endsWith("]");
    }

    private Optional<GitConfigNode> processGitConfigNodeLines(List<String> lines) {
        Map<String, String> properties = new HashMap<>();
        String nodeType = null;
        String nodeName = null;

        for (String line : lines) {
            if (isGitConfigNodeStart(line)) {
                String lineWithoutBrackets = line.replace("[", "").replace("]", "");
                String[] pieces = lineWithoutBrackets.split(" ");

                if (pieces.length == 1) {
                    nodeType = pieces[0].trim();
                } else if (pieces.length == 2) {
                    nodeType = pieces[0].trim();
                    nodeName = pieces[1].replace("\"", "").trim();
                } else {
                    logger.warn(String.format("Invalid git config node. Skipping. %s", line));
                    break;
                }
            } else {
                String[] pieces = line.split("=");

                if (pieces.length == 2) {
                    String propertyKey = pieces[0].trim();
                    String propertyValue = pieces[1].trim();
                    properties.put(propertyKey, propertyValue);
                } else {
                    logger.warn(String.format("Invalid git config nodes property. Skipping. %s", line));
                }
            }
        }

        if (StringUtils.isNotBlank(nodeType)) {
            return Optional.of(new GitConfigNode(nodeType, nodeName, properties));
        } else {
            return Optional.empty();
        }
    }
}
