package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;

/**
 * Notifies that a certain resource request has somehow failed. For example, the resource
 * is not defined, is "ill-formed", can only be request once, etc. The result is that the resource 
 * cannot be used right now. This should lead to requesting that resource again.
 *  
 * @author Julijan Katic
 *
 */
public final class ResourceRequestFailed extends AbstractResourceRequestEvent {

	public ResourceRequestFailed(final ResourceDemandRequest entity, final double delay) {
		super(entity, delay);
	}

	public ResourceRequestFailed(final ResourceDemandRequest entity) {
		super(entity);
	}

}
