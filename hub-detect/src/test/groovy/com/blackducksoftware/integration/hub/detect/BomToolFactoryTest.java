package com.blackducksoftware.integration.hub.detect;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectPropertyMap;
import com.blackducksoftware.integration.hub.detect.configuration.DetectPropertySource;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchProvider;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchRuleSet;

public class BomToolFactoryTest {

    AnnotationConfigApplicationContext runContext;

    @Before
    public void createSpringContext() {
        ConfigurableEnvironment environment = new StandardEnvironment();
        DetectConfiguration mockConfig = new DetectConfiguration(new DetectPropertySource(environment), new DetectPropertyMap());

        BomToolDependencies bomToolDependencies = Mockito.mock(BomToolDependencies.class);
        bomToolDependencies.detectConfiguration = mockConfig;

        runContext = new AnnotationConfigApplicationContext();
        runContext.setDisplayName("Detect Run Test");
        runContext.register(BomToolBeanConfiguration.class);
        runContext.registerBean(BomToolDependencies.class, () -> { return bomToolDependencies; });
        runContext.refresh();
    }

    @Test
    public void testAllBomToolsAreInSearchRules() {
        BomToolSearchProvider provider = runContext.getBean(BomToolSearchProvider.class);

        BomToolEnvironment mockEnv = Mockito.mock(BomToolEnvironment.class);
        BomToolSearchRuleSet ruleSet = provider.createBomToolSearchRuleSet(mockEnv);

        Assert.assertEquals(BomToolType.values().length, ruleSet.getOrderedBomToolRules().size());
    }

    @Test
    public void testNewBomToolsCreatedEveryTime() {
        BomToolSearchProvider provider = runContext.getBean(BomToolSearchProvider.class);

        BomToolEnvironment mockEnv = Mockito.mock(BomToolEnvironment.class);

        BomToolSearchRuleSet ruleSet1 = provider.createBomToolSearchRuleSet(mockEnv);
        BomToolSearchRuleSet ruleSet2 = provider.createBomToolSearchRuleSet(mockEnv);

        BomTool bomTool1 = ruleSet1.getOrderedBomToolRules().get(0).getBomTool();
        BomTool bomTool2 = ruleSet2.getOrderedBomToolRules().get(0).getBomTool();

        //Sanity check they are the same class
        Assert.assertTrue(bomTool1.getClass().isInstance(bomTool2));
        //And check they are not the same instance
        Assert.assertFalse(bomTool1 == bomTool2);
    }

}
