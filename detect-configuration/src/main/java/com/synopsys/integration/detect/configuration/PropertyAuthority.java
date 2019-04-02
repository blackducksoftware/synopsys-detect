/**
 * detect-configuration
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
package com.synopsys.integration.detect.configuration;

//Property Authority represents the Source of Truth for this property.
//Meaning, if you are not this Authority you should not ask for this property, or you will NOT get the Actual Value.
//Only the Property Authority knows the Actual Value of this property in detect, you should ask the Authority for the Value (more likely call the associated getter)
//An Authority of None indicates that this value will never change outside of configuration and is 'Safe' for anyone to ask for the Actual Value.
public enum PropertyAuthority {
    None,
    DirectoryManager,
    AirGapManager;
}