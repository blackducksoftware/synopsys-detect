package com.synopsys.integration.detectable.detectables.gradle.unit;

import java.io.InputStream;

import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.gradle.parsing.parse.BuildGradleParser;

public class BuildGradleParserTest {

    @Test
    public void test() {
        BuildGradleParser buildGradleParser = new BuildGradleParser(new ExternalIdFactory());
        InputStream buildGradle = BuildGradleParserTest.class.getResourceAsStream("/detectables/unit/gradle/build.gradle");
        try {
            buildGradleParser.parse(buildGradle);
        } catch (MultipleCompilationErrorsException e) {
            System.out.println("");
        }
    }
}
