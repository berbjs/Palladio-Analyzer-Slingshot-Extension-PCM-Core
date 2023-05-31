package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.InfrastructureCallsContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;

public class SEFFInfrastructureCallsProgressed extends AbstractSEFFInterpretationEvent {

	private final InfrastructureCallsContext infraContext;

	public SEFFInfrastructureCallsProgressed(final InfrastructureCallsContext infraContext,
			final SEFFInterpretationContext SEFFContext) {
		super(SEFFContext, 0);
		this.infraContext = infraContext;
	}

	public InfrastructureCallsContext getInfraContext() {
		return infraContext;
	}
}
