package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.pcm.seff.seff_performance.InfrastructureCall;

/**
 *
 * Indicates that the simulation progressed to the next
 * {@link InfrastructureCall}.
 *
 * @author Sarah Stieß
 *
 */
public class SEFFInfrastructureCallsProgressed extends AbstractSEFFInterpretationEvent
		implements SEFFInterpreted {

	public SEFFInfrastructureCallsProgressed(final SEFFInterpretationContext infraChildContext) {
		super(infraChildContext, 0);
	}
}
