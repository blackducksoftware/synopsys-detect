package com.blackducksoftware.integration.hub.packman;

import org.springframework.stereotype.Component;

@Component
public class OutputCleaner {
    public String cleanLineComment(String line, final String commentDelimitter) {
        if (line.contains(commentDelimitter)) {
            final String[] sections = line.split(commentDelimitter);
            if (sections.length > 0) {
                line = sections[0].trim();
            } else {
                line = "";
            }
        }
        return line;
    }

}
