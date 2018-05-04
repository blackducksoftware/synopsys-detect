package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.GradleInspectorRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluator;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.request.Request;
import com.blackducksoftware.integration.hub.request.Response;
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnection;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

@Component
public class GradleInspectorRequirementEvaluator extends RequirementEvaluator<GradleInspectorRequirement> {
    private final Logger logger = LoggerFactory.getLogger(GradleInspectorRequirementEvaluator.class);

    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public ExecutableManager executableManager;

    @Autowired
    public ExecutableRunner executableRunner;

    @Autowired
    Configuration configuration;

    @Autowired
    DocumentBuilder xmlDocumentBuilder;

    private String resolvedInitScript = null;
    private String resolvedVersion = null;
    private boolean hasResolvedInspector = false;

    @Override
    public RequirementEvaluation<String> evaluate(final GradleInspectorRequirement requirement, final EvaluationContext context) {
        try {
            if (!hasResolvedInspector) {
                hasResolvedInspector = true;
                resolvedVersion = resolveInspectorVersion();
                resolvedInitScript = resolveInitScriptPath(resolvedVersion);
            }
            if (resolvedInitScript != null) {
                return RequirementEvaluation.passed( resolvedInitScript);
            }else {
                return RequirementEvaluation.failed(null, "No Gradle inspector was found.");
            }
        }catch (final Exception e) {
            return RequirementEvaluation.error(null);
        }
    }

    @Override
    public Class getRequirementClass() {
        return GradleInspectorRequirement.class;
    }


    String resolveInspectorVersion() {
        if ("latest".equalsIgnoreCase(detectConfiguration.getGradleInspectorVersion())) {
            try {
                Document xmlDocument = null;
                final File airGapMavenMetadataFile = new File(detectConfiguration.getGradleInspectorAirGapPath(), "maven-metadata.xml");
                if (airGapMavenMetadataFile.exists()) {
                    final InputStream inputStream = new FileInputStream(airGapMavenMetadataFile);
                    xmlDocument = xmlDocumentBuilder.parse(inputStream);
                } else {
                    final String mavenMetadataUrl = "http://repo2.maven.org/maven2/com/blackducksoftware/integration/integration-gradle-inspector/maven-metadata.xml";
                    final UnauthenticatedRestConnection restConnection = detectConfiguration.createUnauthenticatedRestConnection(mavenMetadataUrl);
                    final Request request = new Request.Builder().uri(mavenMetadataUrl).build();
                    Response response = null;
                    try {
                        response = restConnection.executeRequest(request);
                        final InputStream inputStream = response.getContent();
                        xmlDocument = xmlDocumentBuilder.parse(inputStream);
                    } finally {
                        if ( null != response) {
                            response.close();
                        }
                    }
                }
                final NodeList latestVersionNodes = xmlDocument.getElementsByTagName("latest");
                final Node latestVersion = latestVersionNodes.item(0);
                logger.info("Resolved gradle inspector version from latest to: ${inspectorVersion}");
                return latestVersion.getTextContent();
            } catch (final Exception e) {
                logger.debug("Exception encountered when resolving latest version of Gradle Inspector, skipping resolution.");
                logger.debug(e.getMessage());
                return detectConfiguration.getGradleInspectorVersion();
            }
        } else {
            return detectConfiguration.getGradleInspectorVersion();
        }
    }

    String resolveInitScriptPath(final String version) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
        final File initScriptFile = detectFileManager.createFile(BomToolType.GRADLE, "init-detect.gradle");
        final Map<String, String> model = new HashMap<>();
        model.put("gradleInspectorVersion", version);
        model.put("excludedProjectNames", detectConfiguration.getGradleExcludedProjectNames());
        model.put("includedProjectNames", detectConfiguration.getGradleIncludedProjectNames());
        model.put("excludedConfigurationNames", detectConfiguration.getGradleExcludedConfigurationNames());
        model.put("includedConfigurationNames", detectConfiguration.getGradleIncludedConfigurationNames());

        try {
            final File gradleInspectorAirGapDirectory = new File(detectConfiguration.getGradleInspectorAirGapPath());
            if (gradleInspectorAirGapDirectory.exists()) {
                model.put("airGapLibsPath", StringEscapeUtils.escapeJava(gradleInspectorAirGapDirectory.getCanonicalPath()));
            }
        } catch (final Exception e) {
            logger.debug("Exception encountered when resolving air gap path for gradle, running in online mode instead");
            logger.debug(e.getMessage());
        }

        if (StringUtils.isNotBlank(detectConfiguration.getGradleInspectorRepositoryUrl())) {
            model.put("customRepositoryUrl", detectConfiguration.getGradleInspectorRepositoryUrl());
        }
        final Template initScriptTemplate = configuration.getTemplate("init-script-gradle.ftl");

        final Writer fileWriter = new FileWriter(initScriptFile);
        initScriptTemplate.process(model, fileWriter);
        fileWriter.close();

        return initScriptFile.getCanonicalPath();
    }
}
