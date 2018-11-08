package com.blackducksoftware.integration.hub.detect;

import javax.xml.parsers.DocumentBuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectPropertyMap;
import com.blackducksoftware.integration.hub.detect.configuration.DetectPropertySource;
import com.blackducksoftware.integration.hub.detect.detector.Detector;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorFactory;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableFinder;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.ArtifactResolver;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.CacheableExecutableFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.AirGapManager;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.DetectorSearchProvider;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.DetectorSearchRuleSet;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

import freemarker.template.Configuration;

public class DetectorFactoryTest {

    AnnotationConfigApplicationContext runContext;

    @Before
    public void createSpringContext() {
        ConfigurableEnvironment environment = new StandardEnvironment();
        DetectConfiguration mockConfig = new DetectConfiguration(new DetectPropertySource(environment), new DetectPropertyMap());

        runContext = new AnnotationConfigApplicationContext();
        runContext.setDisplayName("Detect Run Test");
        runContext.register(DetectorBeanConfiguration.class);
        runContext.getBeanFactory().registerSingleton(Gson.class.getSimpleName(), new Gson());
        runContext.getBeanFactory().registerSingleton(JsonParser.class.getSimpleName(), new JsonParser());
        registerMock(runContext, Configuration.class);
        registerMock(runContext, DocumentBuilder.class);
        registerMock(runContext, ExecutableRunner.class);
        registerMock(runContext, AirGapManager.class);
        registerMock(runContext, ExecutableFinder.class);
        registerMock(runContext, ExternalIdFactory.class);
        registerMock(runContext, DetectFileFinder.class);
        registerMock(runContext, DirectoryManager.class);
        registerMock(runContext, DetectConfiguration.class);
        registerMock(runContext, ConnectionManager.class);
        registerMock(runContext, CacheableExecutableFinder.class);
        registerMock(runContext, ArtifactResolver.class);
        registerMock(runContext, DetectInfo.class);

        runContext.refresh();
    }

    private <T> void registerMock(AnnotationConfigApplicationContext context, Class<T> bean) {
        String name = bean.getSimpleName();
        context.getBeanFactory().registerSingleton(name, Mockito.mock(bean));
    }

    @Test
    public void testNewBomToolsCreatedEveryTime() {
        DetectorFactory detectorFactory = runContext.getBean(DetectorFactory.class);
        DetectorSearchProvider provider = new DetectorSearchProvider(detectorFactory);

        DetectorEnvironment mockEnv = Mockito.mock(DetectorEnvironment.class);

        DetectorSearchRuleSet ruleSet1 = provider.createBomToolSearchRuleSet(mockEnv);
        DetectorSearchRuleSet ruleSet2 = provider.createBomToolSearchRuleSet(mockEnv);

        Detector detector1 = ruleSet1.getOrderedBomToolRules().get(0).getDetector();
        Detector detector2 = ruleSet2.getOrderedBomToolRules().get(0).getDetector();

        //Sanity check they are the same class
        Assert.assertTrue(detector1.getClass().isInstance(detector2));
        //And check they are not the same instance
        Assert.assertFalse(detector1 == detector2);
    }

}
