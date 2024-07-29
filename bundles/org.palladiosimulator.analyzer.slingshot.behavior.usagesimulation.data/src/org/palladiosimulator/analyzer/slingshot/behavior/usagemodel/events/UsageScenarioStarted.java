package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;

public final class UsageScenarioStarted extends AbstractUserChangedEvent {

	public UsageScenarioStarted(final UserInterpretationContext entity, final double delay) {
		super(entity, delay);
	}

}
