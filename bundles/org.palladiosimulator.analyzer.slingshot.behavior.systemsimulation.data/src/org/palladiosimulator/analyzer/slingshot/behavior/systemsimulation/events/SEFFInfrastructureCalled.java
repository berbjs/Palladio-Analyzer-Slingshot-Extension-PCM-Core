package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.GeneralEntryRequest;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEntityChangedEvent;

/**
 * Indicates that an infrastructure got called.
 *
 * Similar to {@code ExternalCallAction} except that the Call is based on
 * infrastructure interface instead of 'normal' interfaces.
 *
 * @author Sarah Stieß
 *
 */
public final class SEFFInfrastructureCalled extends AbstractEntityChangedEvent<GeneralEntryRequest> implements SEFFInterpreted {

	public SEFFInfrastructureCalled(final GeneralEntryRequest entity) {
		super(entity, 0);
	}

}
