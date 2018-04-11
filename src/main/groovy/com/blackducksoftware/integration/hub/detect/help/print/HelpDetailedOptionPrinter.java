package com.blackducksoftware.integration.hub.detect.help.print;

import java.util.stream.Collectors;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.help.DetectOptionHelp;

@Component
public class HelpDetailedOptionPrinter {

    public void printDetailedOption(HelpTextWriter writer, DetectOption detectOption) {
        writer.println("");
        writer.println("Detailed information for " + detectOption.getKey());
        writer.println("");
        writer.println("Property description: " + detectOption.getHelp().description);
        writer.println("Property default value: " + detectOption.getDefaultValue());
        if (detectOption.getAcceptableValues().size() > 0) {
            writer.println("Property acceptable values: " + detectOption.getAcceptableValues().stream().collect(Collectors.joining(", ")));
        }
        writer.println("");
        
        DetectOptionHelp help = detectOption.getHelp();
        if (StringUtils.isNotBlank(help.useCases)) {
            writer.println("Use cases: " + help.useCases);
            writer.println();
        }
        
        if (StringUtils.isNotBlank(help.useCases)) {
            writer.println("Common issues: " + help.issues);
            writer.println();
        }
    }
    
    
}
