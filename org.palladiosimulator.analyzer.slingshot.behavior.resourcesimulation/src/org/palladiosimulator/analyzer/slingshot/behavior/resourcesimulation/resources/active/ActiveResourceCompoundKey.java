package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active;

import java.util.Objects;

import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourcetype.ResourceType;

public final class ActiveResourceCompoundKey {

	private final ResourceContainer resourceContainer;
	private final ResourceType resourceType;

	public ActiveResourceCompoundKey(final ResourceContainer resourceContainer, final ResourceType resourceType) {
		this.resourceContainer = resourceContainer;
		this.resourceType = resourceType;
	}

	/**
	 * @return the resourceContainer
	 */
	public ResourceContainer getResourceContainer() {
		return this.resourceContainer;
	}

	/**
	 * @return the resourceType
	 */
	public ResourceType getResourceType() {
		return this.resourceType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.resourceContainer.getId(), this.resourceType.getId());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ActiveResourceCompoundKey)) {
			return false;
		}
		final ActiveResourceCompoundKey other = (ActiveResourceCompoundKey) obj;
		return Objects.equals(this.resourceContainer.getId(), other.resourceContainer.getId())
				&& Objects.equals(this.resourceType.getId(), other.resourceType.getId());
	}

	@Override
	public String toString() {
		return this.resourceContainer.getId() + ":" + this.resourceType.getId();
	}

	public static ActiveResourceCompoundKey of(final ResourceContainer resourceContainer,
			final ResourceType resourceType) {
		return new ActiveResourceCompoundKey(resourceContainer, resourceType);
	}
}
