package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;

/**
 * The single behavior context holder holds only one behavior as a reference
 * instead of a list. The method of selecting the next behavior model which
 * needs to be interpreted is therefore just returning that single behavior
 * model.
 * <p>
 * For instance, the root context holder does not have multiple models, as there
 * is always only one root.
 * 
 * @author Julijan Katic
 */
public abstract class SingleBehaviorContextHolder extends SeffBehaviorContextHolder {

	/**
	 * Instantiates the single context holder.
	 * 
	 * @param behavior  The single behavior to interpret.
	 * @param successor the successor of the action in which the model lies.
	 * @param parent    the model in which the action itself lies.
	 */
	protected SingleBehaviorContextHolder(final ResourceDemandingBehaviour behavior,
			final Optional<AbstractAction> successor, final Optional<SeffBehaviorWrapper> parent) {
		super(List.of(behavior), successor, parent);
	}

	/**
	 * This method always returns the specified single behavior model.
	 * 
	 * @return the specified model.
	 */
	@Override
	public SeffBehaviorWrapper getCurrentProcessedBehavior() {
		return this.getBehaviors().get(0);
	}

}
