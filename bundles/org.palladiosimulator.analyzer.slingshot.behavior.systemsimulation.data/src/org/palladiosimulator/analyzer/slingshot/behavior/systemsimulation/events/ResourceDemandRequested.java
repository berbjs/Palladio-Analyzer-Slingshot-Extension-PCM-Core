package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;

/**
 * An event holding the {@link ResourceDemandRequest} entity specifying that a
 * certain resource should is requested. This typically is fired by a
 * InternalAction of a SEFF.
 * 
 * @author Julijan Katic
 */
public final class ResourceDemandRequested extends AbstractResourceRequestEvent implements SEFFInterpreted {

	public ResourceDemandRequested(final ResourceDemandRequest entity, final double delay) {
		super(entity, delay);
	}

	public ResourceDemandRequested(final ResourceDemandRequest entity) {
		this(entity, 0);
	}
	
}
