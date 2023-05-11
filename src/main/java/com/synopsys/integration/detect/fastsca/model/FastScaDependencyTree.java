package com.synopsys.integration.detect.fastsca.model;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

/**
 * fastSCA dependency tree.
 * 
 * @author skatzman
 */
public class FastScaDependencyTree {
	private final List<String> dependencyTree;
	
	private final FastScaDependencyType dependencyType;
	
	public FastScaDependencyTree(List<String> dependencyTree, FastScaDependencyType dependencyType) {
		this.dependencyTree = (dependencyTree != null) ? ImmutableList.copyOf(dependencyTree) : ImmutableList.of();
		this.dependencyType = Objects.requireNonNull(dependencyType, "Dependency type must be initialized.");
	}
	
	public List<String> getDependencyTree() {
		return dependencyTree;
	}
	
	public FastScaDependencyType getDependencyType() {
		return dependencyType;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getDependencyTree(), getDependencyType());
	}

	@Override
	public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof FastScaDependencyTree) {
        	FastScaDependencyTree otherFastScaDependencyTree = (FastScaDependencyTree) otherObject;

            return Objects.equals(getDependencyTree(), otherFastScaDependencyTree.getDependencyTree())
            		&& Objects.equals(getDependencyType(), otherFastScaDependencyTree.getDependencyType());
        }

        return false;
	}
}
