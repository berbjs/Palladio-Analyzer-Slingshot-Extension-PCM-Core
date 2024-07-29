package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEntityChangedEvent;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;

import com.google.common.base.Preconditions;

/**
 * The user entry request is used to indicate that an
 * {@link EntryLevelSystemCall} is being performed. A system simulation
 * interpreter can use this to simulate the system's repository and SEFFs.
 * 
 * @author Julijan Katic
 * @version 1.0
 */
public final class UserEntryRequested extends AbstractEntityChangedEvent<UserRequest> {

	private final UserInterpretationContext userInterpretationContext;

	/**
	 * Constructs the event.
	 * 
	 * @param entity  The request entity that can be used within the event.
	 * @param context The context of the user that can be used in order to continue
	 *                interpretation.
	 * @param delay   The delay of the event that should be published.
	 */
	public UserEntryRequested(final UserRequest entity, final UserInterpretationContext context, final double delay) {
		super(entity, delay);
		Preconditions.checkArgument(context != null, "The context must not be null");
		this.userInterpretationContext = context;
	}

	/**
	 * Convenience constructor with the default delay which is {@code 0}.
	 * 
	 * @see #UserEntryRequested(UserRequest, UserInterpretationContext, double)
	 */
	public UserEntryRequested(final UserRequest entity, final UserInterpretationContext context) {
		this(entity, context, 0);
	}

	public UserInterpretationContext getUserInterpretationContext() {
		return this.userInterpretationContext;
	}

}
