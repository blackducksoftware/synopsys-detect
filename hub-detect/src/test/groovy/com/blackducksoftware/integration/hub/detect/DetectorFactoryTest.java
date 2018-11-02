package com.blackducksoftware.integration.hub.detect;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectPropertyMap;
import com.blackducksoftware.integration.hub.detect.configuration.DetectPropertySource;
import com.blackducksoftware.integration.hub.detect.detector.Detector;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.DetectorSearchProvider;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.DetectorSearchRuleSet;

public class DetectorFactoryTest {

    AnnotationConfigApplicationContext runContext;

    @Before
    public void createSpringContext() {
        ConfigurableEnvironment environment = new StandardEnvironment();
        DetectConfiguration mockConfig = new DetectConfiguration(new DetectPropertySource(environment), new DetectPropertyMap());

        runContext = new AnnotationConfigApplicationContext();
        runContext.setDisplayName("Detect Run Test");
        runContext.register(BomToolBeanConfiguration.class);
        runContext.refresh();
    }

    @Test
    public void testNewBomToolsCreatedEveryTime() {
        DetectorSearchProvider provider = runContext.getBean(DetectorSearchProvider.class);

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
