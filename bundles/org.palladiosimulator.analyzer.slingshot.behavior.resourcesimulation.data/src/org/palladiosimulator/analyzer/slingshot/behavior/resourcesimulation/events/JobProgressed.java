package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;

/**
 * Specifies that the job or the context containing jobs has somehow progressed.
 * This means that a job could be finished, but there are still other jobs left
 * to be handled.
 * 
 * @author Julijan Katic
 */
public class JobProgressed extends AbstractJobEvent {

	public JobProgressed(final Job entity, final double delay) {
		super(entity, delay);
	}

}
