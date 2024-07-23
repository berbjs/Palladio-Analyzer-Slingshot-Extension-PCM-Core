package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEntityChangedEvent;

/**
 * This abstract class represents an EntityChangedEvent specifically for the
 * {@link UserInterpretationContext} entity.
 * 
 * @author Julijan Katic
 */
public abstract class AbstractUserChangedEvent extends AbstractEntityChangedEvent<UserInterpretationContext> {

	public AbstractUserChangedEvent(final UserInterpretationContext entity, final double delay) {
		super(entity, delay);
	}

}
