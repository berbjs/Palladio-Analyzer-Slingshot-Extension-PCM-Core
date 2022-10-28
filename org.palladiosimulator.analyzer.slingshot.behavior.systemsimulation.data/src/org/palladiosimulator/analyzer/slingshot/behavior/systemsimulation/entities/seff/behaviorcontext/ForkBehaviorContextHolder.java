package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;

/**
 * Fork actions consist of multiple behaviors that are interpreted concurrently.
 * The concurrency is achieved by interpreting one action from one model at a
 * time, and then pointing to the next model. Because each model is wrapped
 * inside a {@link SeffBehaviorWrapper}, the actions can be tracked.
 * 
 * Finished models (as in {@link SeffBehaviorWrapper#hasFinished()}) will be
 * skipped. The action itself is said to be finished if every fork model has
 * finished.
 * 
 * @author Julijan Katic
 *
 */
public final class ForkBehaviorContextHolder extends MultiBehaviorContextHolder {

	private final List<SeffBehaviorWrapper> unfinishedBehaviors;
	private final Iterator<SeffBehaviorWrapper> iterator;
	private SeffBehaviorWrapper currentSeff;

	/**
	 * Instantiates a ForkBehaviorContextHolder. None of the parameters must be
	 * {@code null}.
	 * 
	 * @param behaviors The list of inner behaviors to interpret concurrently.
	 * @param successor The successor action after this one.
	 * @param parent    The model in which the action lies.
	 */
	public ForkBehaviorContextHolder(final List<ResourceDemandingBehaviour> behaviors, final AbstractAction successor,
			final SeffBehaviorWrapper parent) {
		super(behaviors, Optional.of(successor), Optional.of(parent));
		this.unfinishedBehaviors = new ArrayList<>(this.getBehaviors());
		this.iterator = this.unfinishedBehaviors.iterator();
		this.currentSeff = this.iterator.next();
	}

	@Override
	public SeffBehaviorWrapper getCurrentProcessedBehavior() {
		final SeffBehaviorWrapper holder = this.currentSeff;
		this.updateCurrentBehaviorIndex();
		return holder;
	}

	/**
	 * Updates the index to let it point to the next unfinished behavior model.
	 */
	private void updateCurrentBehaviorIndex() {
		this.currentSeff = this.iterator.next();
		while (this.currentSeff.hasFinished()) {
			this.iterator.remove();
			this.currentSeff = this.iterator.next();
		}
	}
}
