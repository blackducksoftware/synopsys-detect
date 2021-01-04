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
package com.synopsys.integration.detect.workflow.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventSystem {
    private final Map<EventType, List<EventListener>> eventListenerMap = new HashMap<>();

    public <T> void publishEvent(final EventType<T> event, final T payload) {
        for (final EventListener listener : safelyGetListeners(event)) {
            listener.eventOccured(payload);
        }
    }

    public <T> void registerListener(final EventType<T> event, final EventListener<T> listener) {
        safelyGetListeners(event).add(listener);
    }

    public <T> void unregisterListener(final EventType<T> event, final EventListener<T> listener) {
        safelyGetListeners(event).remove(listener);
    }

    private List<EventListener> safelyGetListeners(final EventType event) {
        final List<EventListener> listeners;
        if (eventListenerMap.containsKey(event)) {
            listeners = eventListenerMap.get(event);
        } else {
            listeners = new ArrayList<>();
            eventListenerMap.put(event, listeners);
        }
        return listeners;
    }
}
