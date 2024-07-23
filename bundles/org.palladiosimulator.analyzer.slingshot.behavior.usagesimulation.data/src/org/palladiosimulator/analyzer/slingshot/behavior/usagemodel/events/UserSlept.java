package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;

/**
 * This event indicates that the user is now asleep due to a Delay-UserAction
 * within the scenario behavior. The delay of this event is directly set to 0.
 * <p>
 * As long as {@link UserWokeUp}-event has not occurred, the user of should not
 * do anything.
 * 
 * @author Julijan Katic
 *
 */
public final class UserSlept extends AbstractUserChangedEvent {

	public UserSlept(final UserInterpretationContext context) {
		super(context, 0);
	}

}
