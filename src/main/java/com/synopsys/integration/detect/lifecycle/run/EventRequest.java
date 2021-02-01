/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detect.lifecycle.run;

import com.synopsys.integration.detect.workflow.event.EventType;

public class EventRequest<T> {
    private final EventType<T> eventType;
    private final T payload;

    public static <T> EventRequest<T> of(EventType<T> eventType, T payload) {
        return new EventRequest<>(eventType, payload);
    }

    private EventRequest(EventType<T> eventType, T payload) {
        this.eventType = eventType;
        this.payload = payload;
    }

    public EventType<T> getEventType() {
        return eventType;
    }

    public T getPayload() {
        return payload;
    }
}
