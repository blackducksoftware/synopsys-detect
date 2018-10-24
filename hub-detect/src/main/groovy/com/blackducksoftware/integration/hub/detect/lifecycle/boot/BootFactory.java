package com.blackducksoftware.integration.hub.detect.lifecycle.boot;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.blackducksoftware.integration.hub.detect.Application;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.synopsys.integration.blackduck.service.HubServicesFactory;
import com.synopsys.integration.util.IntegrationEscapeUtil;

import freemarker.template.Configuration;

//Responsible for creating a few classes boot needs
public class BootFactory {
    public Gson createGson() {
        return HubServicesFactory.createDefaultGsonBuilder().setPrettyPrinting().create();
    }

    public JsonParser createJsonParser() {
        return new JsonParser();
    }

    public Configuration createConfiguration() {
        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_26);
        configuration.setClassForTemplateLoading(Application.class, "/");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLogTemplateExceptions(true);

        return configuration;
    }

    public DocumentBuilder createXmlDocumentBuilder() {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            return factory.newDocumentBuilder();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public IntegrationEscapeUtil createIntegrationEscapeUtil() {
        return new IntegrationEscapeUtil();
    }
}
