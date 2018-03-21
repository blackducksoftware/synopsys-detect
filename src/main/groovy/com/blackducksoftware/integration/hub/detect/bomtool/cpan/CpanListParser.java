package com.blackducksoftware.integration.hub.detect.bomtool.cpan;

import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CpanListParser {
    private final Logger logger = LoggerFactory.getLogger(CpanListParser.class);

    public Map<String, NameVersionNode> parse(List<String> listText) {
        Map<String, NameVersionNode> moduleMap = new HashMap<>();

        for (String line: listText) {
            if (StringUtils.isBlank(line)) {
                continue;
            }

            if (StringUtils.countMatches(line, "\t") != 1 || line.trim().contains(" ")) {
                continue;
            }

            try {
                String[] module = line.trim().split("\t");
                NameVersionNode nameVersionNode = new NameVersionNode();
                nameVersionNode.setName(module[0].trim());
                nameVersionNode.setVersion(module[1].trim());
                moduleMap.put(nameVersionNode.getName(), nameVersionNode);
            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                logger.debug(String.format("Failed to handle the following line:%s",line));
            }
        }

        return moduleMap;
    }

}
