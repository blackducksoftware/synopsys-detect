/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.bitbake.model;

import java.util.List;

public class BitbakeRecipe {
    private final String name;
    private final List<Layer> layers;

    public BitbakeRecipe(final String name, final List<Layer> layers) {
        this.name = name;
        this.layers = layers;
    }

    public String getName() {
        return name;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public static class Layer {
        private final String layerName;
        private final String componentVersion;

        public Layer(final String layerName, final String componentVersion) {
            this.layerName = layerName;
            this.componentVersion = componentVersion;
        }

        public String getLayerName() {
            return layerName;
        }

        // We have decided to use the version from the .dot files for now.
        // Keeping this around in case we need it down the road. JM - 10/2019
        public String getComponentVersion() {
            return componentVersion;
        }
    }
}
