/*
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

public class SamlIdentityProviderAttributes extends PolarisComponent {
    @SerializedName("entity-id")
    private String entityId;

    @SerializedName("saml-email-domains")
    private List<Object> samlEmailDomains = null;

    @SerializedName("signature-algorithm")
    private String signatureAlgorithm;

    @SerializedName("slo-url")
    private String sloUrl;

    @SerializedName("sso-url")
    private String ssoUrl;

    @SerializedName("x509-cert-base64")
    private String x509CertBase64;

    /**
     * Get entityId
     * @return entityId
     */
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(final String entityId) {
        this.entityId = entityId;
    }

    public SamlIdentityProviderAttributes addSamlEmailDomainsItem(final Object samlEmailDomainsItem) {
        if (this.samlEmailDomains == null) {
            this.samlEmailDomains = new ArrayList<>();
        }
        this.samlEmailDomains.add(samlEmailDomainsItem);
        return this;
    }

    /**
     * Get samlEmailDomains
     * @return samlEmailDomains
     */
    public List<Object> getSamlEmailDomains() {
        return samlEmailDomains;
    }

    public void setSamlEmailDomains(final List<Object> samlEmailDomains) {
        this.samlEmailDomains = samlEmailDomains;
    }

    /**
     * Get signatureAlgorithm
     * @return signatureAlgorithm
     */
    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(final String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    /**
     * Get sloUrl
     * @return sloUrl
     */
    public String getSloUrl() {
        return sloUrl;
    }

    public void setSloUrl(final String sloUrl) {
        this.sloUrl = sloUrl;
    }

    /**
     * Get ssoUrl
     * @return ssoUrl
     */
    public String getSsoUrl() {
        return ssoUrl;
    }

    public void setSsoUrl(final String ssoUrl) {
        this.ssoUrl = ssoUrl;
    }

    /**
     * Get x509CertBase64
     * @return x509CertBase64
     */
    public String getX509CertBase64() {
        return x509CertBase64;
    }

    public void setX509CertBase64(final String x509CertBase64) {
        this.x509CertBase64 = x509CertBase64;
    }

}

