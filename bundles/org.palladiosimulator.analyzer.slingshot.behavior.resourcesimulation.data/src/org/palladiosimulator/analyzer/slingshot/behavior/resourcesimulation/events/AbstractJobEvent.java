package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.ActiveJob;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEntityChangedEvent;

/**
 * This abstract class is designed for events that notify a change in the
 * {@link ActiveJob} entity.
 * 
 * @author Julijan Katic
 */
public abstract class AbstractJobEvent extends AbstractEntityChangedEvent<Job> {

	public AbstractJobEvent(final Job entity, final double delay) {
		super(entity, delay);
	}

}
