package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;

/**
 * Indicates that the user has now woken up from the Delay-action and is now
 * able to further walk the scenario behavior model. The {@code delay} should be
 * set to the StoEx evaluation of the delay event.
 * <p>
 * This event should only be published if the event {@link UserSlept} event has
 * been published directly before.
 * 
 * @author Julijan Katic
 *
 */
public final class UserWokeUp extends AbstractUserChangedEvent {

	public UserWokeUp(final UserInterpretationContext context, final double delay) {
		super(context, delay);
	}

}
