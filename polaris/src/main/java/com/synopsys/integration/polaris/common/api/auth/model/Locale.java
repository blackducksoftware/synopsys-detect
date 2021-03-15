/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class Locale extends PolarisComponent {
    @SerializedName("language")
    private String language;

    @SerializedName("script")
    private String script;

    @SerializedName("country")
    private String country;

    @SerializedName("variant")
    private String variant;

    @SerializedName("extensionKeys")
    private List<String> extensionKeys = null;

    @SerializedName("unicodeLocaleAttributes")
    private List<String> unicodeLocaleAttributes = null;

    @SerializedName("unicodeLocaleKeys")
    private List<String> unicodeLocaleKeys = null;

    @SerializedName("iso3Language")
    private String iso3Language;

    @SerializedName("iso3Country")
    private String iso3Country;

    @SerializedName("displayLanguage")
    private String displayLanguage;

    @SerializedName("displayScript")
    private String displayScript;

    @SerializedName("displayCountry")
    private String displayCountry;

    @SerializedName("displayVariant")
    private String displayVariant;

    @SerializedName("displayName")
    private String displayName;

    /**
     * Get language
     * @return language
     */
    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    /**
     * Get script
     * @return script
     */
    public String getScript() {
        return script;
    }

    public void setScript(final String script) {
        this.script = script;
    }

    /**
     * Get country
     * @return country
     */
    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    /**
     * Get variant
     * @return variant
     */
    public String getVariant() {
        return variant;
    }

    public void setVariant(final String variant) {
        this.variant = variant;
    }

    public Locale addExtensionKeysItem(final String extensionKeysItem) {
        if (this.extensionKeys == null) {
            this.extensionKeys = new ArrayList<>();
        }
        this.extensionKeys.add(extensionKeysItem);
        return this;
    }

    /**
     * Get extensionKeys
     * @return extensionKeys
     */
    public List<String> getExtensionKeys() {
        return extensionKeys;
    }

    public void setExtensionKeys(final List<String> extensionKeys) {
        this.extensionKeys = extensionKeys;
    }

    public Locale addUnicodeLocaleAttributesItem(final String unicodeLocaleAttributesItem) {
        if (this.unicodeLocaleAttributes == null) {
            this.unicodeLocaleAttributes = new ArrayList<>();
        }
        this.unicodeLocaleAttributes.add(unicodeLocaleAttributesItem);
        return this;
    }

    /**
     * Get unicodeLocaleAttributes
     * @return unicodeLocaleAttributes
     */
    public List<String> getUnicodeLocaleAttributes() {
        return unicodeLocaleAttributes;
    }

    public void setUnicodeLocaleAttributes(final List<String> unicodeLocaleAttributes) {
        this.unicodeLocaleAttributes = unicodeLocaleAttributes;
    }

    public Locale addUnicodeLocaleKeysItem(final String unicodeLocaleKeysItem) {
        if (this.unicodeLocaleKeys == null) {
            this.unicodeLocaleKeys = new ArrayList<>();
        }
        this.unicodeLocaleKeys.add(unicodeLocaleKeysItem);
        return this;
    }

    /**
     * Get unicodeLocaleKeys
     * @return unicodeLocaleKeys
     */
    public List<String> getUnicodeLocaleKeys() {
        return unicodeLocaleKeys;
    }

    public void setUnicodeLocaleKeys(final List<String> unicodeLocaleKeys) {
        this.unicodeLocaleKeys = unicodeLocaleKeys;
    }

    /**
     * Get iso3Language
     * @return iso3Language
     */
    public String getIso3Language() {
        return iso3Language;
    }

    public void setIso3Language(final String iso3Language) {
        this.iso3Language = iso3Language;
    }

    /**
     * Get iso3Country
     * @return iso3Country
     */
    public String getIso3Country() {
        return iso3Country;
    }

    public void setIso3Country(final String iso3Country) {
        this.iso3Country = iso3Country;
    }

    /**
     * Get displayLanguage
     * @return displayLanguage
     */
    public String getDisplayLanguage() {
        return displayLanguage;
    }

    public void setDisplayLanguage(final String displayLanguage) {
        this.displayLanguage = displayLanguage;
    }

    /**
     * Get displayScript
     * @return displayScript
     */
    public String getDisplayScript() {
        return displayScript;
    }

    public void setDisplayScript(final String displayScript) {
        this.displayScript = displayScript;
    }

    /**
     * Get displayCountry
     * @return displayCountry
     */
    public String getDisplayCountry() {
        return displayCountry;
    }

    public void setDisplayCountry(final String displayCountry) {
        this.displayCountry = displayCountry;
    }

    /**
     * Get displayVariant
     * @return displayVariant
     */
    public String getDisplayVariant() {
        return displayVariant;
    }

    public void setDisplayVariant(final String displayVariant) {
        this.displayVariant = displayVariant;
    }

    /**
     * Get displayName
     * @return displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

}

