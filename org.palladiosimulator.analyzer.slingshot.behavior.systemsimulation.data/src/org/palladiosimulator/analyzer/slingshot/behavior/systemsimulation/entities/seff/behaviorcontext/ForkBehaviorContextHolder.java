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
 * @author Julijan Katic, Floriment Klinaku, Sarah Stiess
 *
 */
public final class ForkBehaviorContextHolder extends MultiBehaviorContextHolder {

	private final List<SeffBehaviorWrapper> unfinishedBehaviors;
	private Iterator<SeffBehaviorWrapper> iterator;
	private SeffBehaviorWrapper currentSeff;
	private boolean processingMarker = false;
	
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
		if(!this.iterator.hasNext())
		{
			this.iterator = this.unfinishedBehaviors.iterator();
		}
		
		this.currentSeff = this.iterator.next();
		while (this.currentSeff.hasFinished()) {
			this.iterator.remove();
			this.currentSeff = this.iterator.next();
		}
	}
	
	/**
	 * Mark the Fork context holder as processed.
	 */
	public void markProcessed() {
		processingMarker = true;
	}
	
	/**
	 * Returns whether the interpretation is processed in this context. The Fork context 
	 * holder is said to be processed if all children {@link SeffBehaviorWrapper} have
	 * finished and the interpretation has continued to the parent already and has been marked
	 * through markProcessed().
	 * 
	 * 
	 * @return true, if has been marked processed.
	 * @see markProcessed()
	 *         
	 **/
	public boolean isProcessed() {
		return processingMarker;
	}
	
}
