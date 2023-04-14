package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;

public final class ActiveResourceStateUpdated extends AbstractJobEvent {

	private final long queueLength;
	//private final double resourceAmountDemanded;

	public ActiveResourceStateUpdated(final Job entity, final long queueLength) {
		super(entity, 0.0);
		this.queueLength = queueLength;
	}

	public long getQueueLength() {
		return queueLength;
	}

	public double getResourceDemandRequested() {
		return 3.141592;
	}

}
