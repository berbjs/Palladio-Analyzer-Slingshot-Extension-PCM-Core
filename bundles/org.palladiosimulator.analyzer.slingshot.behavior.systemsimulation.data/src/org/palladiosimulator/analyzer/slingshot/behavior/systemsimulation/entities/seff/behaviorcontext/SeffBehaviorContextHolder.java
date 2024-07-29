package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.palladiosimulator.analyzer.slingshot.common.utils.Logic;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.CallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;

import com.google.common.base.Preconditions;

/**
 * The context holder holds information about the model to interpret. A model
 * can, for example, have a "parent model" or even a successor if it is a child
 * model.
 * <p>
 * Here, a child model is typically a model inside an action. For instance, the
 * Branch action consists of at least two inner SEFF behavior models. The model
 * in which the branch action lies is the parent model of both SEFF behavior
 * models. The successor action of the branch action is also the successor
 * action of these two models.
 * <p>
 * As of now, for each such action there exists a corresponding concrete class
 * implementation. If the model is not a "inner model", then it is a root model
 * with no parent and successor action. This especially means that if there is a
 * successor defined, then it must be a child model.
 * <p>
 * The context holder also defines how the different behavior models are
 * interpreted. For instance, if the action is a fork action, then the behavior
 * model are interpreted concurrently. In
 * {@link #getCurrentProcessedBehavior()}, the way of selecting the next model
 * to interpret is defined.
 *
 * @author Julijan Katic
 */
public abstract class SeffBehaviorContextHolder {

	private final List<SeffBehaviorWrapper> behaviors;
	private final Optional<AbstractAction> successor;
	private final Optional<SeffBehaviorWrapper> parent;

	/**
	 * Constructs the behavior context holder. If the behavior context has a
	 * successor defined, then this must be a child context holder.
	 *
	 * @param behaviors the list of resource demanding behaviors that are referenced
	 *                  here. They will be mapped to {@link SeffBehaviorWrapper}s
	 *                  each.
	 * @param successor The successor action after this model if it exists.
	 * @param parent    The parent of this model if it exists.
	 */
	protected SeffBehaviorContextHolder(
			final List<ResourceDemandingBehaviour> behaviors,
			final Optional<AbstractAction> successor,
			final Optional<SeffBehaviorWrapper> parent) {
		Preconditions.checkArgument(Logic.implies(successor.isPresent(), parent.isPresent()));
		this.behaviors = behaviors.stream()
				.map(behavior -> new SeffBehaviorWrapper(behavior, this))
				.collect(Collectors.toList());
		this.successor = successor;
		this.parent = parent;
	}

	/**
	 * Returns the successor action of this model.
	 *
	 * @return the successor action optional.
	 */
	public Optional<AbstractAction> getSuccessor() {
		return this.successor;
	}

	/**
	 * Returns whether the interpretation has finished in this context. The context
	 * is said to be finished if each referenced {@link SeffBehaviorWrapper} is
	 * finished.
	 *
	 * @return false if there is a {@link SeffBehaviorWrapper} that is not finished
	 *         yet, oterhwise true.
	 * @see SeffBehaviorWrapper#hasFinished()
	 */
	public boolean hasFinished() {
		return !this.behaviors.stream().anyMatch(holder -> !holder.hasFinished());
	}

	/**
	 * Returns the common parent of the models.
	 *
	 * @return the common parent of the models if it exists.
	 */
	public Optional<SeffBehaviorWrapper> getParent() {
		return this.parent;
	}

	/**
	 * Returns whether this context holder is a child of another SEFF behavior. If
	 * this is true, then {@link #getParent()} will returns a non-empty
	 * SeffBehaviorWrapper.
	 *
	 * @return true if a parent was set.
	 */
	public boolean isChild() {
		return this.parent.isPresent();
	}

	/**
	 * Returns the next action to be interpreted. This depends of the current
	 * processing behavior. Also, this method allows for iteration over the actions.
	 *
	 * Depending on the behaviour context, the next Action can be an
	 * {@link AbstractAction} as well as an {@link CallAction}. Sadly, their
	 * smallest common parent type is {@link Entity}.
	 *
	 * @return the abstract action to interpret.
	 * @throws NoSuchElementException if the model has already finished (see
	 *                                {@link #hasFinished()}.
	 * @see #getCurrentProcessedBehavior()
	 */
	public Entity getNextAction() {
		if (this.hasFinished()) {
			throw new NoSuchElementException("There is no action to interpret anymore, as this model has finished.");
		}
		return this.getCurrentProcessedBehavior().next();
	}

	/**
	 * Returns the list of referenced behaviors.
	 *
	 * @return the list of behaviors.
	 */
	protected List<SeffBehaviorWrapper> getBehaviors() {
		return this.behaviors;
	}

	/**
	 * Returns the current behavior model that is being interpreted. This method can
	 * also be used for iteration; that is, every time this method is called,
	 * another behavior model will be referenced. Depending on the action kind (i.e.
	 * fork vs branch), a different method of selecting the next or right behavior
	 * model is used.
	 * <p>
	 * Typically, this behavior model will consist the action to be interpreted
	 * next.
	 *
	 * @return the behavior model to interpret.
	 */
	public abstract SeffBehaviorWrapper getCurrentProcessedBehavior();
}
