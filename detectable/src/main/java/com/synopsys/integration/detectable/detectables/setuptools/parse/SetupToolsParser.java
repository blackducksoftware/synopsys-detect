package com.synopsys.integration.detectable.detectables.setuptools.parse;

import java.io.IOException;

public interface SetupToolsParser {

    public SetupToolsParsedResult parse() throws IOException;
    
}
