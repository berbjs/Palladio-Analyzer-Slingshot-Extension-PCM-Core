package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;

/**
 * Event describing the conclusion of the SEFF behavior. This is the event
 * related to the {@code StopAction} action. This event is useful insofar that
 * the handler can restart the SEFF behavior if, for instance, the it is a child
 * behavior within a {@code LoopAction}, or is called from another SEFF.
 * 
 * @author Julijan Katic
 */
public final class SEFFInterpretationFinished extends AbstractSEFFInterpretationEvent {

	public SEFFInterpretationFinished(final SEFFInterpretationContext context) {
		super(context, 0);
	}

}
