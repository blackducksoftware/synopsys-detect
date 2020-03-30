package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeFileType;

public class BitbakeTasksParser {

    public static void main(String[] args) throws IOException {

        File taskDepends = new File("/Users/crowley/Documents/source/detect/synopsys-detect/detectable/src/test/resources/detectables/functional/bitbake/task-depends.dot");
        final InputStream dependsFileInputStream = FileUtils.openInputStream(taskDepends);
        final GraphParser graphParser = new GraphParser(dependsFileInputStream);
        GraphParserTransformer graphParserTransformer = new GraphParserTransformer();
        graphParserTransformer.transform(graphParser, BitbakeFileType.RECIPE_DEPENDS);

    }

}
