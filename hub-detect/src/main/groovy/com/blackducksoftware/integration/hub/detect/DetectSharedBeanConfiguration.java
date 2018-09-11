package com.blackducksoftware.integration.hub.detect;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.context.annotation.Bean;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.synopsys.integration.blackduck.service.HubServicesFactory;

import freemarker.template.Configuration;

public class DetectSharedBeanConfiguration {
    @Bean
    public Gson gson() {
        return HubServicesFactory.createDefaultGsonBuilder().setPrettyPrinting().create();
    }

    @Bean
    public JsonParser jsonParser() {
        return new JsonParser();
    }

    @Bean
    public Configuration configuration() {
        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_26);
        configuration.setClassForTemplateLoading(BeanConfiguration.class, "/");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLogTemplateExceptions(true);

        return configuration;
    }

    @Bean
    public DocumentBuilder xmlDocumentBuilder() throws ParserConfigurationException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        return factory.newDocumentBuilder();
    }

}
