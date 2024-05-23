package org.palladiosimulator.analyzer.slingshot.behavior.usageevolution.events;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.scaledl.usageevolution.Usage;

public class IntervalPassed extends AbstractSimulationEvent {

	private final Usage usage;

	public IntervalPassed(final double interval, final Usage usage) {
		super(interval);
		this.usage = usage;
	}

	public Usage getUsage() {
		return usage;
	}

}
