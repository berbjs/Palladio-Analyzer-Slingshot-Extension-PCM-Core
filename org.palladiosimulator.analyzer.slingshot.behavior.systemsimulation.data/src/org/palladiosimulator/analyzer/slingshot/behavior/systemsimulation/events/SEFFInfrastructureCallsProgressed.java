package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.InfrastructureCallsContext;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEntityChangedEvent;
import org.palladiosimulator.pcm.seff.seff_performance.InfrastructureCall;

/**
 *
 * Indicates that the simulation progressed to the next
 * {@link InfrastructureCall}.
 *
 * @author Sarah Stieß
 *
 */
public class SEFFInfrastructureCallsProgressed extends AbstractEntityChangedEvent<InfrastructureCallsContext>
		implements SEFFInterpreted {

	public SEFFInfrastructureCallsProgressed(final InfrastructureCallsContext infraContext) {
		super(infraContext, 0);
	}
}
