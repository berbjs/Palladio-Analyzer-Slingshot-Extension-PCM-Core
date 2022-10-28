package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEntityChangedEvent;

/**
 * This is the base abstract event especially designed for the interpretation of
 * the SEFF behaviors. It holds the {@link SEFFInterpretationContext} event.
 * 
 * @author Julijan Katic
 *
 */
public abstract class AbstractSEFFInterpretationEvent extends AbstractEntityChangedEvent<SEFFInterpretationContext> implements SEFFInterpreted {

	public AbstractSEFFInterpretationEvent(final SEFFInterpretationContext entity, final double delay) {
		super(entity, delay);
	}

}
