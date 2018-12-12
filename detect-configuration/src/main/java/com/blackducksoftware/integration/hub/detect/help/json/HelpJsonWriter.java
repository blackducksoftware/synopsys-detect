package com.blackducksoftware.integration.hub.detect.help.json;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.help.DetectOptionHelp;
import com.google.gson.Gson;

import freemarker.template.Configuration;

public class HelpJsonWriter {
    private final Logger logger = LoggerFactory.getLogger(com.blackducksoftware.integration.hub.detect.help.html.HelpHtmlWriter.class);

    private final Configuration configuration;
    private final Gson gson;

    public HelpJsonWriter(final Configuration configuration, Gson gson) {
        this.configuration = configuration;
        this.gson = gson;
    }

    public void writeGsonDocument(final String filename, List<DetectOption> detectOptions) {
        final HelpJsonData data = new HelpJsonData();

        for (DetectOption option : detectOptions) {
            HelpJsonOption helpJsonOption = convertOption(option);
            data.options.add(helpJsonOption);
        }

        try {
            try (Writer writer = new FileWriter(filename)) {
                gson.toJson(data, writer);
            }

            logger.info(filename + " was created in your current directory.");
        } catch (final IOException e) {
            logger.error("There was an error when creating the html file", e);
        }
    }

    public HelpJsonOption convertOption(DetectOption detectOption) {
        HelpJsonOption helpJsonOption = new HelpJsonOption();

        DetectProperty property = detectOption.getDetectProperty();
        helpJsonOption.propertyKey = property.getPropertyName();
        helpJsonOption.propertyType = property.getPropertyType().getDisplayName();
        helpJsonOption.addedInVersion = property.getAddedInVersion();
        helpJsonOption.defaultValue = property.getDefaultValue();

        DetectOptionHelp optionHelp = detectOption.getDetectOptionHelp();
        helpJsonOption.group = optionHelp.primaryGroup;
        helpJsonOption.additionalGroups = optionHelp.groups;
        helpJsonOption.description = optionHelp.description;
        helpJsonOption.detailedDescription = optionHelp.detailedHelp;
        helpJsonOption.deprecated = optionHelp.isDeprecated;
        if (optionHelp.isDeprecated) {
            helpJsonOption.deprecatedDescription = optionHelp.deprecation;
            helpJsonOption.deprecatedFailInVersion = optionHelp.deprecationFailInVersion.getDisplayValue();
            helpJsonOption.deprecatedRemoveInVersion = optionHelp.deprecationRemoveInVersion.getDisplayValue();
        }
        helpJsonOption.strictValues = detectOption.hasStrictValidation();
        helpJsonOption.caseSensitiveValues = detectOption.hasCaseSensitiveValidation();
        helpJsonOption.acceptableValues = detectOption.getValidValues();
        helpJsonOption.hasAcceptableValues = detectOption.getValidValues().size() > 0;
        return helpJsonOption;
    }
}
