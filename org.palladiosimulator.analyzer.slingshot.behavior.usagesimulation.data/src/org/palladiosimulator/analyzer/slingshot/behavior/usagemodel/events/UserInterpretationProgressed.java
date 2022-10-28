package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEntityChangedEvent;

/**
 * This event indicates that the interpretation of the usage simulation has
 * progressed. This is useful in so far that if no other event has happened
 * within interpretation, then this can be used as a "default" mechanism to
 * further interpret the system.
 * 
 * @author Julijan Katic
 *
 */
public final class UserInterpretationProgressed extends AbstractEntityChangedEvent<UserInterpretationContext> {

	public UserInterpretationProgressed(final UserInterpretationContext entity, final double delay) {
		super(entity, delay);
	}

	/**
	 * Constructs this event with zero delay.
	 * 
	 * @param entity The entity of this event.
	 */
	public UserInterpretationProgressed(final UserInterpretationContext entity) {
		this(entity, 0);
	}

}
