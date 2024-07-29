package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;

import com.google.common.base.Preconditions;

/**
 * This class is specifically for loop actions. It keeps track of the
 * progression of the loop. The loop behavior is considered to be finished as
 * soon as the inner model has finished and the progression reaches the
 * specified loop count.
 *
 * @author Julijan Katic
 *
 */
public final class LoopBehaviorContextHolder extends SingleBehaviorContextHolder {

	private static final Logger LOGGER = Logger.getLogger(LoopBehaviorContextHolder.class);

	private final int maximalLoopCounter;
	private int progression = 1;

	/**
	 * Instantiates the LoopBehaviorContextHolder. No parameter must be
	 * {@code null}.
	 *
	 * @param behavior           The behavior to interpret.
	 * @param successor          The successor action after this action.
	 * @param parent             The model in which the action lies.
	 * @param maximalLoopCounter The loop count that needs to be reached. Should be
	 *                           {@code >= 0}.
	 */
	public LoopBehaviorContextHolder(final ResourceDemandingBehaviour behavior, final AbstractAction successor,
			final SeffBehaviorWrapper parent, final int maximalLoopCounter) {
		super(behavior, Optional.of(successor), Optional.of(parent));
		Preconditions.checkArgument(maximalLoopCounter >= 0, "The loop counter must be >= 0");
		this.maximalLoopCounter = maximalLoopCounter;
	}

	/**
	 * Returns whether the loop behavior has finished. This is only the case if all
	 * models are finished and the progression has reached the specified loop
	 * counter.
	 *
	 * @return true if each model is finished and the loop counter has been reached.
	 */
	@Override
	public boolean hasFinished() {
		LOGGER.debug("Progression: " + this.progression + " -- Maximum: " + this.maximalLoopCounter);
		return super.hasFinished() && this.progression == this.maximalLoopCounter;
	}

	@Override
	public AbstractAction getNextAction() {
		LOGGER.info("Retrieve next action in loop -- progression: " + this.progression);
		/* If the model is finished but the counter hasn't been reached yet, repeat the scenario. */
		if (this.getCurrentProcessedBehavior().hasFinished() && this.progression < this.maximalLoopCounter) {
			this.progression++;
			this.getCurrentProcessedBehavior().repeatScenario();
		}
		// Loops has no InfrastructureCalls next Action is always AbstractAction
		return (AbstractAction) super.getNextAction();
	}
}
