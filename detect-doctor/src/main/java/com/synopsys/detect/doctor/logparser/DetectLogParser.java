package com.synopsys.detect.doctor.logparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetectLogParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static String CONFIGURATION_MARKER = "------------------------------------------------------------";
    private static String EXTRACTION_START_MARKER = "Extracting";
    private static String EXTRACTION_SEPERATOR = "------------------------------------------------------------------------------------------------------";
    private static String DETECT_START_MARKER = "INFO  [main] --- Configuration processed completely.";
    private static String DETECT_VERSION_MARKER = "Detect Version: ";

    private static String PROJECT_NAME_MARKER = "Project Name: ";
    private static String PROJECT_VERSION_MARKER = "Project Version Name: ";

    private DetectLogPropertyParser logPropertyParser = new DetectLogPropertyParser();
    private DetectExtractionParser extractionParser = new DetectExtractionParser();

    private enum ConfigurationParseState {
        NOT_STARTED,
        PRE_CONFIGURATION,
        IN_CONFIGURATION,
        POST_CONFIGURATION
    }

    private enum ExtractionParseState {
        NONE,
        IN_HEADER,
        IN_BODY,
        IN_FOOTER
    }

    public DetectLogParseResult parse(File file) {
        DetectLogParseResult parseResult = new DetectLogParseResult();
        parseResult.loggedConfiguration = new LoggedDetectConfiguration();
        parseResult.loggedConfiguration.loggedPropertyList = new ArrayList<>();

        LoggedDetectExtraction currentExtraction = null;

        ConfigurationParseState configurationState = ConfigurationParseState.PRE_CONFIGURATION;
        ExtractionParseState extractionState = ExtractionParseState.NONE;

        String previousLine = "";
        String line = "";
        try {
            try (FileInputStream dependenciesInputStream = new FileInputStream(file); BufferedReader reader = new BufferedReader(new InputStreamReader(dependenciesInputStream, StandardCharsets.UTF_8));) {
                while (reader.ready()) {
                    previousLine = line;
                    line = reader.readLine();

                    if (configurationState == ConfigurationParseState.NOT_STARTED) {
                        if (line.contains(DETECT_START_MARKER)) {
                            configurationState = ConfigurationParseState.PRE_CONFIGURATION;
                            continue;
                        }
                    }

                    if (configurationState == ConfigurationParseState.PRE_CONFIGURATION) {
                        if (line.equalsIgnoreCase(CONFIGURATION_MARKER)) {
                            configurationState = ConfigurationParseState.IN_CONFIGURATION;
                            continue;
                        }
                        if (line.contains(DETECT_VERSION_MARKER)) {
                            parseResult.loggedConfiguration.detectVersion = DoctorStringUtils.substringAfter(line, DETECT_VERSION_MARKER);
                        }
                    } else if (configurationState == ConfigurationParseState.IN_CONFIGURATION) {
                        if (line.equalsIgnoreCase(CONFIGURATION_MARKER)) {
                            configurationState = ConfigurationParseState.POST_CONFIGURATION;
                            continue;
                        } else {
                            parseConfigurationLine(line, parseResult);
                            continue;
                        }
                    }

                    if (configurationState == ConfigurationParseState.POST_CONFIGURATION) {
                        if (extractionState == ExtractionParseState.NONE) {
                            if (line.contains(EXTRACTION_SEPERATOR) && previousLine.contains(EXTRACTION_START_MARKER)) {
                                extractionState = ExtractionParseState.IN_HEADER;
                                currentExtraction = new LoggedDetectExtraction();
                            }
                        } else if (extractionState == ExtractionParseState.IN_HEADER) {
                            if (line.contains(EXTRACTION_SEPERATOR)) {
                                extractionState = ExtractionParseState.IN_BODY;
                            } else {
                                extractionParser.parseExtractionHeader(currentExtraction, line);
                            }
                        } else if (extractionState == ExtractionParseState.IN_BODY) {
                            if (line.contains(EXTRACTION_SEPERATOR)) {
                                extractionState = ExtractionParseState.IN_FOOTER;
                            } else {
                                extractionParser.parseExtractionBody(currentExtraction, line);
                            }
                        } else if (extractionState == ExtractionParseState.IN_FOOTER) {
                            if (line.contains(EXTRACTION_SEPERATOR)) {
                                extractionState = ExtractionParseState.NONE;
                                parseResult.loggedConfiguration.extractions.add(currentExtraction);
                            } else {
                                extractionParser.parseExtractionFooter(currentExtraction, line);
                            }
                        }
                    }

                    if (configurationState == ConfigurationParseState.POST_CONFIGURATION && extractionState == ExtractionParseState.NONE) {
                        //parse something else?
                    }
                }
            }
            parseResult.success = true;
        } catch (IOException e) {
            e.printStackTrace();
            parseResult.success = false;
        }
        return parseResult;
    }

    private void parseConfigurationLine(String line, DetectLogParseResult result) {
        LoggedDetectProperty property = logPropertyParser.parseProperty(line);
        if (property != null) {
            result.loggedConfiguration.loggedPropertyList.add(property);
        }
    }

}
