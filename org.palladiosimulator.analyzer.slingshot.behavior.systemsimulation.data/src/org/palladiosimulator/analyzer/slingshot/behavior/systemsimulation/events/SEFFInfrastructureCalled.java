package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.GeneralEntryRequest;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEntityChangedEvent;

/**
 * Tells that a Infrastructure is called. Very similar to external calls.
 * Typically, this event will be published when an {@code InfrastructureCall}
 * occurs insides an {@code InternalAction}.
 *
 * @author Sarah Stieß
 *
 */
public final class SEFFInfrastructureCalled extends AbstractEntityChangedEvent<GeneralEntryRequest> implements SEFFInterpreted {

	public SEFFInfrastructureCalled(final GeneralEntryRequest entity) {
		super(entity, 0);
	}

}
