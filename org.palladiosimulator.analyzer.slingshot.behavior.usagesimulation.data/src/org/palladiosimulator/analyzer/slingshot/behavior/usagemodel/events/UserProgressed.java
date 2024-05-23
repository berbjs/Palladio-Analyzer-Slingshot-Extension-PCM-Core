package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;

public class UserProgressed extends AbstractUserChangedEvent {

    public UserProgressed(UserInterpretationContext entity) {
	super(entity, 0);
    }

}
