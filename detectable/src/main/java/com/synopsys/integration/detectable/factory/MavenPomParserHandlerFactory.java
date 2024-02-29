package com.synopsys.integration.detectable.factory;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.maven.cli.parse.MavenPomParserHandler;

public class MavenPomParserHandlerFactory {

    private static ExternalIdFactory externalIdFactory;

    public MavenPomParserHandlerFactory(ExternalIdFactory externalIdFactory){
        this.externalIdFactory = externalIdFactory;
    }
    public MavenPomParserHandler getMavenPomParserHandler(){
        return new MavenPomParserHandler(externalIdFactory);
    }

}
