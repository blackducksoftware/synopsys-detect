package com.synopsys.integration.detect.fastsca.model;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * fastSCA evidence.
 * 
 * @author skatzman
 */
public class FastScaEvidence {
	private final FastScaDependency dependency;
	
	private final Set<FastScaDependencyTree> dependencyTrees;
	
	public FastScaEvidence(FastScaDependency dependency, Set<FastScaDependencyTree> dependencyTrees) {
		Objects.requireNonNull(dependency, "Dependency must be initialized.");
		
		this.dependency = dependency;
		this.dependencyTrees = (dependencyTrees != null) ? ImmutableSet.copyOf(dependencyTrees) : ImmutableSet.of();
	}

	public FastScaDependency getDependency() {
		return dependency;
	}
	
	public Set<FastScaDependencyTree> getDependencyTrees() {
		return dependencyTrees;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getDependency(), getDependencyTrees());
	}
	
	@Override
	public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof FastScaEvidence) {
        	FastScaEvidence otherFastScaEvidence = (FastScaEvidence) otherObject;

            return Objects.equals(getDependency(), otherFastScaEvidence.getDependency())
                    && Objects.equals(getDependencyTrees(), otherFastScaEvidence.getDependencyTrees());
        }

        return false;
	}
}
