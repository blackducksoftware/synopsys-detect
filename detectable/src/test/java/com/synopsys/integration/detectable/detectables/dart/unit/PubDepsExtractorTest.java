package com.synopsys.integration.detectable.detectables.dart.unit;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.dart.pubdep.DartPubDepsDetectableOptions;
import com.synopsys.integration.detectable.detectables.dart.pubdep.PubDepsExtractor;
import com.synopsys.integration.detectable.detectables.dart.pubdep.PubDepsParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.ToolVersionLogger;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

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
