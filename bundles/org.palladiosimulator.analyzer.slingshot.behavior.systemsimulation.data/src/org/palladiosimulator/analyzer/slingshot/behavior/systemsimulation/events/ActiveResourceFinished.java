package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;

/**
 * Tells that the active resource has finished its work. The SEFF may or may not
 * wait for this event to happen before continuing, depending on the resource
 * type.
 * 
 * @author Julijan Katic
 *
 */
public final class ActiveResourceFinished extends AbstractResourceRequestEvent {

	public ActiveResourceFinished(final ResourceDemandRequest entity, final double delay) {
		super(entity, delay);
	}

	public ActiveResourceFinished(final ResourceDemandRequest entity) {
		super(entity);
	}

}
