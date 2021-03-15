/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.service;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.polaris.common.api.PolarisComponent;
import com.synopsys.integration.rest.response.Response;

public class PolarisJsonTransformer {
    private static final String FIELD_NAME_POLARIS_COMPONENT_JSON = "json";

    private final Gson gson;
    private final IntLogger logger;

    public PolarisJsonTransformer(final Gson gson, final IntLogger logger) {
        this.gson = gson;
        this.logger = logger;
    }

    public <C extends PolarisComponent> C getResponse(final Response response, final Type responseType) throws IntegrationException {
        final String json = response.getContentString();
        return getResponseAs(json, responseType);
    }

    public <C extends PolarisComponent> C getResponseAs(final String json, final Type responseType) throws IntegrationException {
        try {
            final JsonObject jsonElement = gson.fromJson(json, JsonObject.class);
            return getResponseAs(jsonElement, responseType);
        } catch (final JsonSyntaxException e) {
            logger.error(String.format("Could not parse the provided json with Gson:%s%s", System.lineSeparator(), json));
            throw new IntegrationException(e.getMessage(), e);
        }
    }

    public <C extends PolarisComponent> C getResponseAs(final JsonObject jsonObject, final Type responseType) throws IntegrationException {
        final String json = gson.toJson(jsonObject);
        try {
            addJsonAsField(jsonObject);
            return gson.fromJson(jsonObject, responseType);
        } catch (final JsonSyntaxException e) {
            logger.error(String.format("Could not parse the provided jsonElement with Gson:%s%s", System.lineSeparator(), json));
            throw new IntegrationException(e.getMessage(), e);
        }
    }

    private void addJsonAsField(final JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            final JsonObject innerObject = jsonElement.getAsJsonObject();
            jsonElement.getAsJsonObject().addProperty(FIELD_NAME_POLARIS_COMPONENT_JSON, gson.toJson(innerObject));
            for (final Map.Entry<String, JsonElement> innerObjectFields : innerObject.entrySet()) {
                addJsonAsField(innerObjectFields.getValue());
            }
        } else if (jsonElement.isJsonArray()) {
            for (final JsonElement arrayElement : jsonElement.getAsJsonArray()) {
                addJsonAsField(arrayElement);
            }
        }
    }

}
