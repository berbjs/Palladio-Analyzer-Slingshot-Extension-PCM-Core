package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;

/**
 * This class wraps a {@link ResourceDemandingBehaviour} with further actions
 * and implements an iterator with the order of actions as defined in the model.
 * <p>
 * The list of actions itself does not have to be ordered in the same way as in
 * the model. This class, however, accomplishes this.
 * <p>
 * This class furthermore offers a way of repeating the scenario again from the
 * start action by calling the {@link #repeatScenario()} method.
 * 
 * @author Julijan Katic
 */
public final class SeffBehaviorWrapper implements Iterator<AbstractAction> {

	private final ResourceDemandingBehaviour behavior;
	private final SeffBehaviorContextHolder context;
	private AbstractAction currentAction;
	private boolean finished;

	/**
	 * Instantiates this class with the corresponding behavior and the context that
	 * references this class. It also sets the current action to the Start action in
	 * this scenario. If such a start action does not exist, an
	 * {@link IllegalStateException} will be thrown.
	 * 
	 * @param behavior The behavior with actions.
	 * @param context  The context referencing this class.
	 * @throws IllegalStateException if there is no start action within the behavior
	 *                               model.
	 */
	public SeffBehaviorWrapper(final ResourceDemandingBehaviour behavior, final SeffBehaviorContextHolder context) {
		super();
		this.behavior = behavior;
		this.context = context;
		this.setCurrentActionToStart();
	}

	/**
	 * Returns the behavior wrapped by this class.
	 * 
	 * @return the behavior
	 */
	public ResourceDemandingBehaviour getBehavior() {
		return this.behavior;
	}

	/**
	 * Returns the current action that should be interpreted.
	 * 
	 * @return the current action.
	 */
	public AbstractAction getCurrentAction() {
		return this.currentAction;
	}

	/**
	 * Returns the context that is referencing this iterator.
	 * 
	 * @return the referencing context.
	 */
	public SeffBehaviorContextHolder getContext() {
		return this.context;
	}

	/**
	 * Returns whether the model has finished. The model is considered to be
	 * finished if the iterator points to the {@code StopAction} of the model.
	 * 
	 * @return true if the iterator points to the {@code StopAction}.
	 */
	public boolean hasFinished() {
		return this.finished;
	}

	/**
	 * Sets the pointer again to the start action.
	 */
	public void repeatScenario() {
		this.setCurrentActionToStart();
	}

	/**
	 * Helper method that sets the current action to the first start action within
	 * this model. If such an action does not exist, an IllegalStateException will
	 * be thrown.
	 * 
	 * @throws IllegalStateException if the model does not have a start action.
	 */
	private void setCurrentActionToStart() {
		this.currentAction = this.behavior.getSteps_Behaviour().stream()
				.filter(StartAction.class::isInstance)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("This model does not have a start action."));
		this.finished = false;
	}

	/**
	 * Checks whether the model can be further iterated. This means that the model
	 * is not finished (as in {@link #hasFinished()}) and that there is an successor
	 * action in the currently pointed action.
	 * 
	 * @return true if has not finished yet and there is a successor.
	 */
	@Override
	public boolean hasNext() {
		return !this.hasFinished() && this.currentAction != null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws NoSuchElementException if there is no successor action (see
	 *                                {@link #hasNext()}).
	 */
	@Override
	public AbstractAction next() {
		if (!this.hasNext()) {
			throw new NoSuchElementException(
					"There is no action after StopAction, or a successor action was not defined.");
		}

		final AbstractAction result = this.currentAction;
		this.finished = result instanceof StopAction;
		this.currentAction = this.currentAction.getSuccessor_AbstractAction();
		return result;
	}

}
