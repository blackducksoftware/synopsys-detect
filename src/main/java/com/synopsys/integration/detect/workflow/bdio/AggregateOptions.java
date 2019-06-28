/**
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.bdio;

import java.util.Optional;

public class AggregateOptions {

    private Optional<String> aggregateName;
    private boolean uploadEmptyAggregate;

    public AggregateOptions(final Optional<String> aggregateName, final boolean uploadEmptyAggregate) {
        this.aggregateName = aggregateName;
        this.uploadEmptyAggregate = uploadEmptyAggregate;
    }

    public static AggregateOptions doNotAggregate() {
        return new AggregateOptions(Optional.empty(), false);
    }

    public static AggregateOptions aggregateAndAlwaysUpload(String aggregateName) {
        return new AggregateOptions(Optional.of(aggregateName), true);
    }

    public static AggregateOptions aggregateButSkipEmpty(String aggregateName) {
        return new AggregateOptions(Optional.of(aggregateName), false);
    }

    public Optional<String> getAggregateName() {
        return aggregateName;
    }

    public boolean shouldUploadEmptyAggregate() {
        return uploadEmptyAggregate;
    }

    public boolean shouldAggregate() {
        return aggregateName.isPresent();
    }
}

