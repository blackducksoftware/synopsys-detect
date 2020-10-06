/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
import com.synopsys.integration.detectable.detectable.file.WildcardFileFinder;

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
        FileFinder finder = new WildcardFileFinder();
        Assert.assertEquals(4, finder.findFiles(sourceFile, "*", 2).size());
    }

    @Test
    public void testDefaultsFindAll() throws IOException {
        FileFinder finder = fileFinderFromProperty(DetectProperties.DETECT_DETECTOR_SEARCH_EXCLUSION_DEFAULTS.getProperty(), "true");
        Assert.assertEquals(4, finder.findFiles(sourceFile, "*", 2).size());
    }

    @Test
    public void testFirstFileExcluded() throws IOException {
        FileFinder finder = fileFinderFromProperty(DetectProperties.DETECT_DETECTOR_SEARCH_EXCLUSION_FILES.getProperty(), firstFileDotTxt);
        Assert.assertEquals(3, finder.findFiles(sourceFile, "*", 2).size());
    }
}
