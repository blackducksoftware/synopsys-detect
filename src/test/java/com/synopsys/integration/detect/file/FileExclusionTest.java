package com.synopsys.integration.detect.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.types.path.SimplePathResolver;
import com.synopsys.integration.configuration.util.ConfigTestUtils;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.file.impl.SimpleFileFinder;

public class FileExclusionTest {
    File sourceFile;
    Path sourcePath;

    //these tests are based around these three files - DO NOT CHANGE without also changing tests.
    String firstFileDotTxt = "first-file.txt";
    String secondFileDotDat = "second-file.dat";
    String folder = "folder";
    String thirdDotTxt = "third.txt"; //third is inside folder.

    @Before
    public void beforeEachTestMethod() throws IOException {
        sourcePath = Files.createTempDirectory("exclusion-test");
        sourceFile = sourcePath.toFile();

        FileUtils.writeStringToFile(new File(sourcePath.toFile(), firstFileDotTxt), "", Charset.defaultCharset());
        FileUtils.writeStringToFile(new File(sourcePath.toFile(), secondFileDotDat), "", Charset.defaultCharset());
        File folderFile = new File(sourcePath.toFile(), folder);
        Assert.assertTrue(folderFile.mkdir());
        FileUtils.writeStringToFile(new File(folderFile, thirdDotTxt), "", Charset.defaultCharset());
    }

    private FileFinder fileFinderFromProperty(Property prop, String value) {
        PropertyConfiguration propertyConfiguration = ConfigTestUtils.configOf(Pair.of(prop.getKey(), value));
        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(propertyConfiguration, new SimplePathResolver());
        return detectConfigurationFactory.createFilteredFileFinder(sourcePath);
    }

    @Test
    public void testSimpleFindsAll() throws IOException {
        FileFinder finder = new SimpleFileFinder();
        Assert.assertEquals(4, finder.findFiles(sourceFile, "*", 2).size());
    }

    @Test
    public void testDefaultsFindAll() throws IOException {
        FileFinder finder = fileFinderFromProperty(DetectProperties.Companion.getDETECT_DETECTOR_SEARCH_EXCLUSION_DEFAULTS(), "true");
        Assert.assertEquals(4, finder.findFiles(sourceFile, "*", 2).size());
    }

    @Test
    public void testFirstFileExcluded() throws IOException {
        FileFinder finder = fileFinderFromProperty(DetectProperties.Companion.getDETECT_DETECTOR_SEARCH_EXCLUSION_FILES(), firstFileDotTxt);
        Assert.assertEquals(3, finder.findFiles(sourceFile, "*", 2).size());
    }

    @Test
    public void testFolderExcluded() throws IOException {
        FileFinder finder = fileFinderFromProperty(DetectProperties.Companion.getDETECT_DETECTOR_SEARCH_EXCLUSION(), folder);
        Assert.assertEquals(3, finder.findFiles(sourceFile, "*", 2).size());
    }

    @Test
    public void testFolderExcludedWithPattern() throws IOException {
        FileFinder finder = fileFinderFromProperty(DetectProperties.Companion.getDETECT_DETECTOR_SEARCH_EXCLUSION_PATTERNS(), folder.substring(0, 3) + "*");
        Assert.assertEquals(3, finder.findFiles(sourceFile, "*", 2).size());
    }

    @Test
    public void testFolderExcludedWithPath() throws IOException {
        FileFinder finder = fileFinderFromProperty(DetectProperties.Companion.getDETECT_DETECTOR_SEARCH_EXCLUSION_PATHS(), folder);
        Assert.assertEquals(3, finder.findFiles(sourceFile, "*", 2).size());
    }
}
