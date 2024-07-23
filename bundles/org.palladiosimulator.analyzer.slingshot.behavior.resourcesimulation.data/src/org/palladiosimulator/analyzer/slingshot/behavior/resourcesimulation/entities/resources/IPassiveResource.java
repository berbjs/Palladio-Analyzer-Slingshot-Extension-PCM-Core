package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.PassiveResource;

/**
 * This interface holds all information (data) about a resource. It does not
 * have any commands to the resource.
 * 
 * @author Julijan Katic
 */
public interface IPassiveResource extends IResource {

	/**
	 * Returns the model element which is represented by this (virtual) passive
	 * resource.
	 * 
	 * @return The passive resource model element.
	 */
	public PassiveResource getPCMPassiveResource();

	/**
	 * Returns the model element in which the passive resource lies.
	 * 
	 * @return The assembly context in which the passive resource lies.
	 */
	public AssemblyContext getAssemblyContext();

	/**
	 * Returns the number of remaining instances.
	 * 
	 * @return A non-negative number of remaining instances.
	 */
	public long getCurrentlyAvailable();

}
