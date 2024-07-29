package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.pcm.usagemodel.Stop;

/**
 * This event indicates that the user has been aborted during the simulation. It
 * marks out the case in which the interpretation for a user could not proceed
 * due to changes in the model.
 * 
 * @author Floriment Klinaku
 *
 */
public class UserAborted extends AbstractUserChangedEvent {

	public UserAborted(UserInterpretationContext entity) {
		super(entity, 0);
		// TODO Auto-generated constructor stub
	}

}
