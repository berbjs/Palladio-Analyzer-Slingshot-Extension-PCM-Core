package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;

/**
 * Specifies that the job has been added to the running jobs.
 * 
 * @author Julijan Katic
 */
public final class JobInitiated extends AbstractJobEvent {

	public JobInitiated(final Job entity, final double delay) {
		super(entity, delay);
	}

	public JobInitiated(final Job entity) {
		super(entity, 0);
	}

}
