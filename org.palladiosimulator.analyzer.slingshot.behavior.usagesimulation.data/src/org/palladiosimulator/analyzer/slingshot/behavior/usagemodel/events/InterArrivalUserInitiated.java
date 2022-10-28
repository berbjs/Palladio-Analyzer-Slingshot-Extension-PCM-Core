package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;

/**
 * Event that is used to initialize a new open workload user after a certain
 * time. The time must be specified in the {@link #getDelay()}.
 * 
 * @author Julijan Katic
 *
 */
public final class InterArrivalUserInitiated extends AbstractSimulationEvent {

	/**
	 * Constructs this event with a delay.
	 * 
	 * @param delay The delay after what a user should be (re-)spawned.
	 */
	public InterArrivalUserInitiated(final double delay) {
		super(delay);
	}

}
