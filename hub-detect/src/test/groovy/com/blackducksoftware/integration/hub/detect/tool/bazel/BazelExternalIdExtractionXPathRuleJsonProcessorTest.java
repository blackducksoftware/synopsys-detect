package com.blackducksoftware.integration.hub.detect.tool.bazel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

public class BazelExternalIdExtractionXPathRuleJsonProcessorTest {
    private static File tempDir;

    @BeforeAll
    public static void before() throws IOException {
        Path tempDirPath = Files.createTempDirectory("BazelExternalIdExtractionXPathRuleJsonProcessorTest_");
        tempDir = tempDirPath.toFile();
    }

    @Test
    public void test() throws IOException {
        List<SearchReplacePattern> searchReplacePatterns = new ArrayList<>();
        searchReplacePatterns.add(new SearchReplacePattern("search1", "replace1"));
        List<String> bazelQueryCommandArgsIncludingQuery = Arrays.asList("query", "stuff", "-output", "xml");
        BazelExternalIdExtractionXPathRule origRule = new BazelExternalIdExtractionXPathRule(
            Arrays.asList("targetDependenciesQueryBazelCmdArgument1", "targetDependenciesQueryBazelCmdArgument2"),
            searchReplacePatterns,
        Arrays.asList("dependencyDetailsXmlQueryBazelCmdArgument1", "dependencyDetailsXmlQueryBazelCmdArgument2"),
        "testXPathQuery", "testRuleElementValueAttrName", "testArtifactStringSeparatorRegex");

        List<BazelExternalIdExtractionXPathRule> origRules = new ArrayList<>();
        origRules.add(origRule);
        Gson gson = new Gson();
        String json = gson.toJson(origRules);
        System.out.printf("json: %s\n", json);
        File jsonFile = new File(tempDir, "rulesFile.json");
        System.out.printf("Writing file %s\n", jsonFile.getAbsolutePath());
        FileUtils.write(jsonFile, json, StandardCharsets.UTF_8);

        BazelExternalIdExtractionXPathRuleJsonProcessor processor = new BazelExternalIdExtractionXPathRuleJsonProcessor(gson);
        List<BazelExternalIdExtractionXPathRule> loadedRules = processor.load(jsonFile);
        for (BazelExternalIdExtractionXPathRule loadedRule : loadedRules) {
            System.out.printf("loaded rule: %s\n", loadedRule);
        }
        assertEquals(1, loadedRules.size());
        assertEquals("testXPathQuery", loadedRules.get(0).getXPathQuery());

        String convertedJson = processor.toJson(loadedRules);
        assertEquals("[{\"targetDependenciesQueryBazelCmdArguments\":[\"targetDependenciesQueryBazelCmdArgument1\",\"targetDependenciesQueryBazelCmdArgument2\"],\"dependencyToBazelExternalIdTransforms\":[{\"searchRegex\":\"search1\",\"replacementString\":\"replace1\"}],\"dependencyDetailsXmlQueryBazelCmdArguments\":[\"dependencyDetailsXmlQueryBazelCmdArgument1\",\"dependencyDetailsXmlQueryBazelCmdArgument2\"],\"xPathQuery\":\"testXPathQuery\",\"ruleElementValueAttrName\":\"testRuleElementValueAttrName\",\"artifactStringSeparatorRegex\":\"testArtifactStringSeparatorRegex\"}]",
            convertedJson);
    }
}
