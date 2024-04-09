package org.palladiosimulator.analyzer.slingshot.behavior.usageevolution.events;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

public class IntervalPassed extends AbstractSimulationEvent {

	private final UsageScenario scenario;

	public IntervalPassed(final double interval, final UsageScenario scenario) {
		super(interval);
		this.scenario = scenario;
	}

	public UsageScenario getScenario() {
		return scenario;
	}

}
