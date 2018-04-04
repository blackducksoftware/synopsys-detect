package com.blackducksoftware.integration.hub.detect.help.print;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.help.print.DetailedDetectOptions.DetailedDetectOption;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

@Component
public class HelpDetailedOptionPrinter {

    @Autowired
    private Gson gson;
    
    public void printDetailedOption(HelpTextWriter writer, DetectOption detectOption) {
        writer.println("");
        writer.println("Detailed information for " + detectOption.getKey());
        writer.println("");
        writer.println("Property description: " + detectOption.getDescription());
        writer.println("Property default value: " + detectOption.getDefaultValue());
        writer.println("");
        
        DetailedDetectOptions detailedOptions = loadOptionDetails(writer);
        DetailedDetectOption detailedOption = detailedOptions.getOptionDetailsOrNull(detectOption);
        if (detailedOption != null) {
            writer.println("Use cases: " + detailedOption.useCases);
            writer.println();
            writer.println("Common issues: " + detailedOption.issues);
            writer.println();
        }
    }
    
    private DetailedDetectOptions loadOptionDetails(HelpTextWriter writer) {
        try {
            final String detectDetailedOptions = getClass().getResource("/detect-options.json").getFile();
            final FileReader fileReader = new FileReader(detectDetailedOptions);
            final JsonReader jsonReader = new JsonReader(fileReader);
            DetailedDetectOptions optionDetails = gson.fromJson(jsonReader, DetailedDetectOptions.class);
            return optionDetails;
        } catch (final FileNotFoundException e) {
            writer.println("Unable to load detailed option data.");
            writer.println("\t" + e.getMessage());
            return null;
        }
    }
    
}
