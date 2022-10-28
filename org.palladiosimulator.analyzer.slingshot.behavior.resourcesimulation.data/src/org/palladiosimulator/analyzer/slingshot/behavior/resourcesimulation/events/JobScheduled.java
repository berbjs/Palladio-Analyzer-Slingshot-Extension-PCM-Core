package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEntityChangedEvent;

/**
 * Specifies that the job has been scheduled for work.
 * 
 * @author Julijan Katic
 */
public final class JobScheduled extends AbstractEntityChangedEvent<Job> {

	public JobScheduled(final Job entity, final double delay) {
		super(entity, delay);
		// TODO Auto-generated constructor stub
	}

}
