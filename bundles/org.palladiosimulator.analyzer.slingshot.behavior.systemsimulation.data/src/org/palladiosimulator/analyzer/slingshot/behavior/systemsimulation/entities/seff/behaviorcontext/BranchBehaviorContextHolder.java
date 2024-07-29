package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext;

import java.util.Optional;

import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;

/**
 * This class is used for handling Branch actions. Although Branch actions
 * consist of multiple sub-behavior models, only one of them will be interpreted
 * at runtime; hence, it is a SingleBehaviorContextHolder.
 * <p>
 * Because it is a Action, it must contain a successor and the model in which
 * the action lies (the parent model).
 * 
 * @author Julijan Katic
 *
 */
public final class BranchBehaviorContextHolder extends SingleBehaviorContextHolder {

	/**
	 * Instantiates the BranchBehaviorContextHolder. No parameter must be
	 * {@code null}.
	 * 
	 * @param behavior  The inner behavior to interpret.
	 * @param successor The successor action after this one.
	 * @param parent    The model in which the branch action lies.
	 */
	public BranchBehaviorContextHolder(final ResourceDemandingBehaviour behavior, final AbstractAction successor,
			final SeffBehaviorWrapper parent) {
		super(behavior, Optional.of(successor), Optional.of(parent));
	}

}
