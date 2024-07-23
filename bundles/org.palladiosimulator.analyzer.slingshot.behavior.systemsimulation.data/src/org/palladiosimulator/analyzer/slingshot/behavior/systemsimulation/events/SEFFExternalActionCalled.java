package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.GeneralEntryRequest;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEntityChangedEvent;

/**
 * Tells that another external SEFF is called. Typically, this event will be published when a 
 * {@code ExternalCallAction} has occurred. 
 * 
 * @author Julijan Katic
 *
 */
public final class SEFFExternalActionCalled extends AbstractEntityChangedEvent<GeneralEntryRequest> implements SEFFInterpreted {

	public SEFFExternalActionCalled(final GeneralEntryRequest entity) {
		super(entity, 0);
	}

}
