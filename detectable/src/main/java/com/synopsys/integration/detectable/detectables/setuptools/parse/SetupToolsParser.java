package com.synopsys.integration.detectable.detectables.setuptools.parse;

import java.io.IOException;

public interface SetupToolsParser {

    public void parse(SetupToolsParsedResult parsedResult) throws IOException;
    
}
