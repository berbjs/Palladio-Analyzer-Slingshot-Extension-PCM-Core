package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;

/**
 * Event to announce the state of an active resource.
 *
 * @author Sarah Stieß
 *
 */
public final class ActiveResourceStateUpdated extends AbstractJobEvent {

	private final long requestsAtResource;
	private final double utilization;

	public ActiveResourceStateUpdated(final Job entity, final long requestsAtResource, final double utilization) {
		super(entity, 0.0);
		this.requestsAtResource = requestsAtResource;
		this.utilization = utilization;
	}

	public long requestsAtResource() {
		return this.requestsAtResource;
	}

	public double utilization() {
		return this.utilization;
	}
}
