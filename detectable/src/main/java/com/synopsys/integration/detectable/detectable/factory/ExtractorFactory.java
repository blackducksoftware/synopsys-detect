/**
 * detectable
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.detectable.detectable.factory;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableRunner;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeExtractor;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeArchitectureParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeGraphTransformer;
import com.synopsys.integration.detectable.detectables.bitbake.parse.GraphParserTransformer;

public class ExtractorFactory {
    private final UtilityFactory utilityFactory;

    public ExtractorFactory(UtilityFactory utilityFactory) {
        this.utilityFactory = utilityFactory;
    }

    public BitbakeExtractor bitbakeExtractor() {
        GraphParserTransformer graphParserTransformer = new GraphParserTransformer();
        BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        BitbakeArchitectureParser bitbakeArchitectureParser = new BitbakeArchitectureParser();
        BitbakeExtractor bitbakeExtractor = new BitbakeExtractor(new SimpleExecutableRunner(), this.utilityFactory.simpleFileFinder(), graphParserTransformer, bitbakeGraphTransformer, bitbakeArchitectureParser);
        return bitbakeExtractor;
    }
}
