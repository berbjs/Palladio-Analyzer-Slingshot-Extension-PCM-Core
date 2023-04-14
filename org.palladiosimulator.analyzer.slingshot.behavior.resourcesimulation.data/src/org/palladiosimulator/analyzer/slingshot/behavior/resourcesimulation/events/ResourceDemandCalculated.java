package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;

public final class ResourceDemandCalculated extends AbstractJobEvent {

	private final double calculatedResourceDemand;

	public ResourceDemandCalculated(final Job entity, final double calculatedResourceDemand) {
		super(entity, 0.0);
		this.calculatedResourceDemand = calculatedResourceDemand;
	}

	public double getResourceDemandRequested() {
		return calculatedResourceDemand;
	}

}
