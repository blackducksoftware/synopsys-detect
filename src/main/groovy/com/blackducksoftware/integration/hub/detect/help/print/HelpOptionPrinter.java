package com.blackducksoftware.integration.hub.detect.help.print;

import java.util.List;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.help.DetectOption;

@Component
public class HelpOptionPrinter {

    public void printOptions(HelpTextWriter writer, List<DetectOption> options, String notes) {
        writer.printColumns("Property Name", "Default", "Description");
        writer.printSeperator();

        if (notes != null) {
            writer.println(notes);
            writer.println();
        }
        
        String group = null;
        for (final DetectOption detectValue : options) {
            final String currentGroup = detectValue.getGroup();
            if (group == null) {
                group = currentGroup;
            } else if (!group.equals(currentGroup)) {
                writer.println();
                group = currentGroup;
            }
            writer.printColumns("--" + detectValue.getKey(), detectValue.getDefaultValue(), detectValue.getDescription());
        }
    }
    
    public void printStandardFooter(HelpTextWriter writer, String groupText) {
        writer.println();
        writer.println("Usage : ");
        writer.println("\t--<property name>=<value>");
        writer.println();
        writer.println("To print only a subset of options, you may specify one of the following printable groups with '-h [group]' or '--help [group]': ");
        writer.println("\t" + groupText);
        writer.println();        
        writer.println("To search options, you may specify a search term followed by * with '-h [term]*' or '--help [term]*': ");
        writer.println();
    }
}
