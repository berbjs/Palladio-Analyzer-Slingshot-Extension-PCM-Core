package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;

/**
 * Event to announce a requested resource demand.
 *
 * @author Sarah Stieß
 *
 */
public final class ResourceDemandCalculated extends AbstractJobEvent {

	private final double calculatedResourceDemand;

	public ResourceDemandCalculated(final Job job, final double calculatedResourceDemand) {
		super(job, 0.0);
		this.calculatedResourceDemand = calculatedResourceDemand;
	}

	public double getResourceDemandRequested() {
		return calculatedResourceDemand;
	}
}
