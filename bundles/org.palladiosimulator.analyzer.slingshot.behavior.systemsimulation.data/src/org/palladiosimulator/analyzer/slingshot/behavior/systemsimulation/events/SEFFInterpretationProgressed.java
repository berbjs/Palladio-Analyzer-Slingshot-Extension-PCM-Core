package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;

/**
 * Event describing that the interpretation of the SEFF behavior has in some way
 * progressed. This should only be used if there are no other events describing
 * the progression. For example, the Start action of a RDSEFF does nothing in
 * particular, but in order to continue the interpretation, this event will be
 * published. However, the Loop action has its own specific event which already
 * includes the progression of the interpretation, and hence this does not and
 * should not be used.
 * <p>
 * The delay of this event is automatically set to {@code 0}. If another delay
 * is needed, another event should be used instead.
 * 
 * @author Julijan Katic
 *
 */
public final class SEFFInterpretationProgressed extends AbstractSEFFInterpretationEvent {

	public SEFFInterpretationProgressed(final SEFFInterpretationContext context) {
		super(context, 0);
	}

}
