package com.synopsys.integration.detect.workflow.blackduck.developer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.workflow.DetectRun;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.file.DirectoryOptions;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckRapidModePostActionsTest {

    @Test
    public void testJsonFileGenerated() throws Exception {
        IntLogger logger = Mockito.mock(IntLogger.class);
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        StatusEventPublisher statusEventPublisher = Mockito.mock(StatusEventPublisher.class);
        ExitCodePublisher exitCodePublisher = Mockito.mock(ExitCodePublisher.class);
        OperationSystem operationSystem = Mockito.mock(OperationSystem.class);
        Path scanOutputPath = Files.createTempDirectory("rapid_scan_output_path");
        DirectoryOptions directoryOptions = new DirectoryOptions(null, null, null, scanOutputPath, null);
        DirectoryManager directoryManager = new DirectoryManager(directoryOptions, new DetectRun(""));

        File expectedOutputFile = new File("src/test/resources/workflow/blackduck/rapid_scan_result_file.json");
        String expectedOutput = FileUtils.readFileToString(expectedOutputFile, StandardCharsets.UTF_8).trim();

        List<DeveloperScanComponentResultView> results = createResults(gson, expectedOutput);

        BlackDuckRapidModePostActions postActions = new BlackDuckRapidModePostActions(logger, gson, statusEventPublisher, exitCodePublisher, directoryManager, operationSystem);
        NameVersion nameVersion = new NameVersion("rapid_scan_post_action", "test");
        postActions.perform(nameVersion, results);

        File actualOutputFile = createActualOutputFile(directoryManager, nameVersion);
        String actualOutput = FileUtils.readFileToString(actualOutputFile, StandardCharsets.UTF_8);

        assertTrue(actualOutputFile.exists());
        assertTrue(StringUtils.isNotBlank(actualOutput));
    }

    private List<DeveloperScanComponentResultView> createResults(Gson gson, String jsonContent) {
        List<DeveloperScanComponentResultView> results = new ArrayList<>();
        JsonArray array = gson.fromJson(jsonContent, JsonArray.class);
        Iterator<JsonElement> iterator = array.iterator();
        while (iterator.hasNext()) {
            JsonElement arrayItem = iterator.next();
            String json = gson.toJson(arrayItem);
            DeveloperScanComponentResultView view = gson.fromJson(arrayItem, DeveloperScanComponentResultView.class);
            view.setJson(json);
            view.setGson(gson);
            view.setJsonElement(arrayItem);
            results.add(view);
        }

        return results;
    }

    private File createActualOutputFile(DirectoryManager directoryManager, NameVersion projectNameVersion) {
        IntegrationEscapeUtil escapeUtil = new IntegrationEscapeUtil();
        String escapedProjectName = escapeUtil.replaceWithUnderscore(projectNameVersion.getName());
        String escapedProjectVersionName = escapeUtil.replaceWithUnderscore(projectNameVersion.getVersion());
        return new File(directoryManager.getScanOutputDirectory(), escapedProjectName + "_" + escapedProjectVersionName + "_BlackDuck_DeveloperMode_Result.json");
    }
}
