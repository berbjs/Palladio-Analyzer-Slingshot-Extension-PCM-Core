package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext;

import java.util.Optional;

import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;

/**
 * This class represents the root SEFF. This does not have a parent and a
 * successor action, as this model is not inside another action.
 * <p>
 * Hence, the methods {@link #getParent()} and {@link #getSuccessor()} will each
 * result in {@link Optional#empty()}.
 * 
 * @author Julijan Katic
 */
public final class RootBehaviorContextHolder extends SingleBehaviorContextHolder {

	/**
	 * Instantiates the RootBehaviorContextHolder. The parent and the successor
	 * action will be set to {@link Optional#empty()}.
	 * 
	 * @param behavior the non-null root SEFF.
	 */
	public RootBehaviorContextHolder(final ResourceDemandingBehaviour behavior) {
		super(behavior, Optional.empty(), Optional.empty());
	}

}
