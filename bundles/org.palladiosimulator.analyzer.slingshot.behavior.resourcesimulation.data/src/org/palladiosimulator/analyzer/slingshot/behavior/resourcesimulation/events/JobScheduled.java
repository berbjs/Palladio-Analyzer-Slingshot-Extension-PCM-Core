package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.ActiveJob;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEntityChangedEvent;

/**
 * Specifies that the job has been scheduled for work.
 * 
 * @author Julijan Katic
 */
public final class JobScheduled extends AbstractEntityChangedEvent<ActiveJob> {

	public JobScheduled(final ActiveJob entity, final double delay) {
		super(entity, delay);
		// TODO Auto-generated constructor stub
	}

}
