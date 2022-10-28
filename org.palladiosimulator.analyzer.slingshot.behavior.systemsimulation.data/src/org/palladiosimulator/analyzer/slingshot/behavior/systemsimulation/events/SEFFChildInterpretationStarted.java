package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.RootBehaviorContextHolder;

/**
 * Notifies that a child or inner SEFF has been started interpreting. The behavior context
 * of the seff interpretation context ({@link SEFFInterpretationContext#getBehaviorContext()})
 * must not be a {@link RootBehaviorContextHolder}, but instead a branch, loop, fork, etc. context.
 * This means that the context must have a parent.
 * 
 * @author Julijan Katic
 */
public final class SEFFChildInterpretationStarted extends AbstractSEFFInterpretationEvent {

	/**
	 * @param context the interpretation context.
	 * @throws IllegalArgumentException if the behavior context of the interpretation context
	 * 									is a {@link RootBehaviorContextHolder}
	 */
	public SEFFChildInterpretationStarted(final SEFFInterpretationContext context) {
		super(context, 0);
		if (context.getBehaviorContext() instanceof RootBehaviorContextHolder) {
			throw new IllegalArgumentException("The child interpretator must have a parent context.");
		}
	}
	
}
