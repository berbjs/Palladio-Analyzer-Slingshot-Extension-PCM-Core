package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.ThinkTime;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;

import com.google.common.base.Preconditions;

/**
 * This event is used for ClosedWorkload users with a think time. This is used
 * after the user has successfully traversed the UsageModel and reached the end.
 * The user then has to re-enter the usage model after the specified think time.
 * <p>
 * The event should therefore only be published after a {@link UserFinished}
 * event.
 * 
 * @author Julijan Katic
 */
public final class ClosedWorkloadUserInitiated extends AbstractUserChangedEvent {

	/**
	 * Initializes the ClosedWorkloadUserInitiated event.
	 * 
	 * @param entity    The context itself with the non-null user.
	 * @param thinkTime The non-{@code null} think time.
	 */
	public ClosedWorkloadUserInitiated(final UserInterpretationContext entity, final ThinkTime thinkTime) {
		super(entity, Preconditions.checkNotNull(thinkTime, "The ThinkTime must not be null.").calculateRV());
	}

}
