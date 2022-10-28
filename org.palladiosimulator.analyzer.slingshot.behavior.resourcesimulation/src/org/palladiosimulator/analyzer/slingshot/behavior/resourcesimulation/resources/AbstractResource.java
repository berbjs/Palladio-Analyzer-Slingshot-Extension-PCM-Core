package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.IResource;

import com.google.common.base.Preconditions;

/**
 * Abstract class that holds the required information for a (active, passive)
 * resource and already implements the method appropriately. This also specifies
 * the {@link #equals(Object)} and {@link #hashCode()} by using the
 * {@link #getId()} instance. This especially means that the {@link #getName()}
 * String does <strong>not</strong> identify the resource instance!
 * 
 * @author Julijan Katic
 */
public abstract class AbstractResource implements IResource {

	private final long capacity;
	private final String name;
	private final Object id;

	/**
	 * Constructs the abstract resource with the required instances.
	 * 
	 * @param capacity The maximum capacity of the resource.
	 * @param name     The name of the resource.
	 * @param id       The unique identity instance for this resource.
	 */
	public AbstractResource(final long capacity, final String name, final Object id) {
		super();
		this.capacity = capacity;
		this.name = name;
		this.id = Preconditions.checkNotNull(id, "The id must not be null for a resource.");
	}

	/**
	 * @return the capacity
	 */
	@Override
	public long getCapacity() {
		return this.capacity;
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * @return the id
	 */
	@Override
	public Object getId() {
		return this.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AbstractResource)) {
			return false;
		}
		final AbstractResource other = (AbstractResource) obj;
		return Objects.equals(this.id, other.id);
	}

	@Override
	public String toString() {
		return "Resource [name=" + this.name + ", id=" + this.id + "]";
	}

}
