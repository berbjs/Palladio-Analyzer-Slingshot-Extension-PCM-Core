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

	public ActiveResourceStateUpdated(final Job entity, final long requestsAtResource) {
		super(entity, 0.0);
		this.requestsAtResource = requestsAtResource;
	}

	public long requestsAtResource() {
		return requestsAtResource;
	}
}
