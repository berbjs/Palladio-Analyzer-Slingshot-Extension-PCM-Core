package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources;

/**
 * Interface of any resource, be it active or passive. Each resource is uniquely
 * identified, has a name and a maximum capacity. The identifier is a
 * {@link Object} that should be implemented appropriately, namely override the
 * {@code equals} and {@code hashCode} methods in order to identify this
 * resource correctly.
 * 
 * @author Julijan Katic
 *
 */
public interface IResource {

	/**
	 * The maximum capacity of this resource. This should return a positive number,
	 * or {@code -1} if it has infinite capacity (see {@link #INFINITE_CAPACITY}).
	 * 
	 * @return a positive number or {@link INFINITE_CAPACITY} specifiying the
	 *         capacity.
	 */
	long getCapacity();

	/**
	 * Returns the name of the resource. Typically, this will be the entity name
	 * which is defined in the model.
	 * 
	 * @return A non-empty string representing the name of the resource.
	 */
	String getName();

	/**
	 * Returns an instance that uniquely identifies this resource. In order for this
	 * to work, the instance should override the {@code equals} and {@code hashCode}
	 * method appropriately, so that if two such instances are equal according to
	 * their {@code equals} method, the two resource instances will also be
	 * considered equal as well.
	 * 
	 * @return A non-{@code null} instance identifying this resource.
	 */
	Object getId();

	/**
	 * Method that clears all the queues of jobs. This should be called only at the
	 * end of simulation in order to clean up.
	 */
	void clearJobs();

	public static final long INFINITE_CAPACITY = -1;
}
