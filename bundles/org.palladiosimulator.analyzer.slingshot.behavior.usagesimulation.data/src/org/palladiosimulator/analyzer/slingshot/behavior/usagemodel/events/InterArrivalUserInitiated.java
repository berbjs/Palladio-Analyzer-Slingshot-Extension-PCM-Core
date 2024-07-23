package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;

/**
 * Event that is used to initialize a new open workload user after a certain
 * time. The time must be specified in the {@link #getDelay()}.
 *
 * @author Julijan Katic
 *
 */
public final class InterArrivalUserInitiated extends AbstractUserChangedEvent {

	/**
	 * Constructs this event with a delay.
	 *
	 * @param delay The delay after what a user should be (re-)spawned.
	 */
	public InterArrivalUserInitiated(final UserInterpretationContext entity, final double delay) {
		super(entity, delay);
	}

}
