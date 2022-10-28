package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;

/**
 * Specifies that the job has been finished and can be safely removed from the
 * list.
 * 
 * @author Julijan Katic
 */
public final class JobFinished extends AbstractJobEvent {

	public JobFinished(final Job entity, final double delay) {
		super(entity, delay);
	}

	public JobFinished(final Job entity) {
		super(entity, 0);
	}

}
