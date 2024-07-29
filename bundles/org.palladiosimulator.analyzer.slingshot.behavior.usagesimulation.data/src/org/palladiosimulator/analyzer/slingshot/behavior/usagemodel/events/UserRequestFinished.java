package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEntityChangedEvent;

/**
 * Event describing that the {@code EntryLevelSystemCall} has finished and it is
 * now possible to continue interpretation in the usage model.
 * 
 * @author Julijan Katic
 */
public final class UserRequestFinished extends AbstractEntityChangedEvent<UserRequest> {

	private final UserInterpretationContext userContext;

	public UserRequestFinished(final UserRequest entity, final UserInterpretationContext userContext) {
		super(entity, 0);
		this.userContext = userContext;
	}

	public UserInterpretationContext getUserContext() {
		return this.userContext;
	}

}
