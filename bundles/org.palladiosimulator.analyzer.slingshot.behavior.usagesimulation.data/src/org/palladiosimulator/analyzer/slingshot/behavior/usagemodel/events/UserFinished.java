package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.pcm.usagemodel.Stop;

/**
 * This event indicates that the user has reached the end of the scenario
 * behavior. The end of the scenario must be defined as {@link Stop}-Action.
 * This means that as soon as the interpreter reached this action, this event
 * will be published.
 * <p>
 * This event should behave according to the PCM standard. For example, for
 * closed workload users, the usagesimulation should be started again for the
 * same user.
 * 
 * @author Julijan Katic
 *
 */
public final class UserFinished extends AbstractUserChangedEvent {

	public UserFinished(final UserInterpretationContext context) {
		super(context, 0);
	}

}
