package com.blackduck.integration.detectable.detectables.dart.unit;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.blackduck.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.blackduck.integration.detectable.detectable.util.EnumListFilter;
import com.blackduck.integration.detectable.detectables.dart.pubdep.DartPubDepsDetectableOptions;
import com.blackduck.integration.detectable.detectables.dart.pubdep.PubDepsExtractor;
import com.blackduck.integration.detectable.detectables.dart.pubdep.PubDepsParser;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.util.ToolVersionLogger;
import com.blackduck.integration.executable.ExecutableOutput;
import com.blackduck.integration.executable.ExecutableRunnerException;

public class PubDepsExtractorTest {
    @Test
    public void testGracefulFailureOnDifferentExecutableOutputs() throws ExecutableRunnerException {
        List<ExecutableOutput> executableOutputs = Arrays.asList(
            new ExecutableOutput(-1, "", ""),
            null
        );
        for (ExecutableOutput executableOutput : executableOutputs) {
            testGracefulFailure(executableOutput);
        }
    }

    private void testGracefulFailure(ExecutableOutput mockExecutableOutput) throws ExecutableRunnerException {
        DetectableExecutableRunner executableRunner = Mockito.mock(DetectableExecutableRunner.class);
        Mockito.when(executableRunner.execute(Mockito.any())).thenReturn(mockExecutableOutput);
        PubDepsExtractor extractor = new PubDepsExtractor(executableRunner, new PubDepsParser(), null, new ToolVersionLogger(executableRunner));
        Extraction extraction = extractor.extract(null, null, null, new DartPubDepsDetectableOptions(EnumListFilter.excludeNone()), null);

        Assertions.assertFalse(extraction.isSuccess() && null == extraction.getError());
    }
}
