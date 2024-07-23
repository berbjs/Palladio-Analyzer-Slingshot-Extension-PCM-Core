package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;

/**
 * This event indicates that the user has started walking the scenario behavior.
 * This means that the user is currently at the Start-UserAction of a scenario
 * behavior.
 * 
 * @author Julijan Katic
 *
 */
public final class UserStarted extends AbstractUserChangedEvent {

	public UserStarted(final UserInterpretationContext context, final double delay) {
		super(context, delay);
	}

	public UserStarted(final UserInterpretationContext context) {
		this(context, 0);
	}

}
