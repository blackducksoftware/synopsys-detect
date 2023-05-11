package com.synopsys.integration.detect.fastsca.model;

import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * fastSCA dependency.
 * 
 * @author skatzman
 */
public class FastScaDependency {
	private final String namespace;
	
	private final String identifier;
	
	public FastScaDependency(String namespace, String identifier) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(namespace), "Namespace must not be null or empty.");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(identifier), "Identifier must not be null or empty.");
		
		this.namespace = namespace;
		this.identifier = identifier;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getIdentifier() {
		return identifier;
	}
	
	public String getComponentSearchTerm() {
		return getNamespace() + ':' + getIdentifier();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getNamespace(), getIdentifier());
	}
	
	@Override
	public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof FastScaDependency) {
        	FastScaDependency otherFastScaDependency = (FastScaDependency) otherObject;

            return Objects.equals(getNamespace(), otherFastScaDependency.getNamespace())
                    && Objects.equals(getIdentifier(), otherFastScaDependency.getIdentifier());
        }

        return false;
	}
}
