package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.passive;

import java.util.Objects;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.PassiveResource;

/**
 * A simple data holder that fuses the {@link PassiveResource} and
 * {@link AssemblyContext} into an identifier. This is done by using the
 * {@code getId()} of each of these components respectively in that order.
 * <p>
 * The class overrides the {@link #equals(Object)} and {@link #hashCode()}
 * accordingly, so that two instances of {@link PassiveResourceCompoundKey} are
 * considered equal if the id's of each of the above mentioned component are
 * equal.
 * 
 * @author Julijan Katic
 *
 */
public final class PassiveResourceCompoundKey {

	/**
	 * The passive resource to identify during simulation, having its own id in the
	 * model.
	 */
	private final PassiveResource passiveResource;

	/** The assembly context in which the passive resource resides. */
	private final AssemblyContext assemblyContext;

	/**
	 * Constructs a new passive resource identifier from the required components.
	 * 
	 * @param passiveResource The (model) passive resource with an (model) id.
	 * @param assemblyContext The assembly context where the resource resides, also
	 *                        having a (model) id.
	 */
	public PassiveResourceCompoundKey(final PassiveResource passiveResource,
			final AssemblyContext assemblyContext) {
		super();
		this.passiveResource = passiveResource;
		this.assemblyContext = assemblyContext;
	}

	/**
	 * @return the passiveResource
	 */
	public PassiveResource getPassiveResource() {
		return this.passiveResource;
	}

	/**
	 * @return the assemblyContext
	 */
	public AssemblyContext getAssemblyContext() {
		return this.assemblyContext;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.passiveResource.getId(), this.assemblyContext.getId());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PassiveResourceCompoundKey)) {
			return false;
		}
		final PassiveResourceCompoundKey other = (PassiveResourceCompoundKey) obj;
		return Objects.equals(this.assemblyContext.getId(), other.assemblyContext.getId())
				&& Objects.equals(this.passiveResource.getId(), other.passiveResource.getId());
	}

	@Override
	public String toString() {
		return this.passiveResource.getId() + ":" + this.assemblyContext.getId();
	}

	/**
	 * Factory method to construct a new id instead of using the constructor.
	 * 
	 * @param passiveResource The (model) passive resource with an (model) id.
	 * @param assemblyContext The assembly context where the resource resides, also
	 *                        having a (model) id.
	 * @return The newly constructed instance id.
	 */
	public static PassiveResourceCompoundKey of(final PassiveResource passiveResource,
			final AssemblyContext assemblyContext) {
		return new PassiveResourceCompoundKey(passiveResource, assemblyContext);
	}
}