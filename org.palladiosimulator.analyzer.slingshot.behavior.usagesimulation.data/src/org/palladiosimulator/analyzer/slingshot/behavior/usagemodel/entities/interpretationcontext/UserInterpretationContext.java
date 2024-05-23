package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext;

import java.util.Optional;

import javax.annotation.processing.Generated;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior.UsageScenarioBehaviorContext;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStackframe;

/**
 * The UserInterpretationContext represents the knowledge that the interpreter
 * needs to continue interpretation for a user.
 *
 * @author Julijan Katic, Sarah Stieß
 */
public abstract class UserInterpretationContext {

	/** The scenario to interpret. */
	private final UsageScenario scenario;

	/** The current action to be interpreted. */
	private final AbstractUserAction currentAction;

	/** The user of the interpretation. */
	private final User user;

	/** The current Run of the usage */
	private final int currentUsageRun;

	/** The behavior context indicating in which scenario we are. */
	private final UsageScenarioBehaviorContext behaviorContext;

	private final SimulatedStackframe<Object> resultFrame;

	@Generated("SparkTools")
	protected UserInterpretationContext(final BaseBuilder<?, ?> builder) {
		this.scenario = builder.scenario;
		this.currentAction = builder.currentAction;
		this.user = builder.user;
		this.currentUsageRun = builder.currentUsageRun;
		this.behaviorContext = builder.usageScenarioBehaviorContext;
		this.resultFrame = builder.resultFrame;
	}

	public SimulatedStackframe<Object> getResultFrame() {
		return this.resultFrame;
	}

	public UsageScenario getScenario() {
		return this.scenario;
	}

	public AbstractUserAction getCurrentAction() {
		return this.currentAction;
	}

	public User getUser() {
		return this.user;
	}

	public int getCurrentUsageRun() {
		return this.currentUsageRun;
	}

	public UserInterpretationContext incrementUsageRun() {
		return this.update().withCurrentUsageRun(this.currentUsageRun + 1).build();
	}

	public UserInterpretationContext updateAction(final AbstractUserAction abstractAction) {
		return this.update().withCurrentAction(abstractAction).build();
	}

	public abstract <T extends UserInterpretationContext, B extends BaseBuilder<T, B>> B update();

	/**
	 * Helper method to create an update builder. The {@link #update()} should use
	 * this method for to connect the updatable parameters from this parent class.
	 *
	 * @param <T>     The type extending this class. Used to inform which sub-class
	 *                is built.
	 * @param <B>     The type extending the abstract builder class
	 *                {@link BaseBuilder} for this. Used to indicate which concrete
	 *                builder class is used.
	 * @param builder The actual concrete builder.
	 * @return The same builder where each parameter of this class is connected to
	 *         the builder.
	 */
	protected final <T extends UserInterpretationContext, B extends BaseBuilder<T, B>> B updateWithBuilder(
			final B builder) {
		return builder.withCurrentAction(this.currentAction).withCurrentUsageRun(this.currentUsageRun)
				.withScenario(this.scenario).withUser(this.user)
//				.withParentContext(this.parentContext)
				.withUsageScenarioBehaviorContext(this.getBehaviorContext());
	}

	public UsageScenarioBehaviorContext getBehaviorContext() {
		return this.behaviorContext;
	}

	/**
	 * Builder to build {@link UserInterpretationContext}.
	 */
	@Generated("SparkTools")
	protected abstract static class BaseBuilder<T extends UserInterpretationContext, B extends BaseBuilder<T, B>> {
		private UsageScenario scenario;
		private AbstractUserAction currentAction;
		private User user;
		private int currentUsageRun;
//		private Optional<UserInterpretationContext> parentContext = Optional.empty();
		private UsageScenarioBehaviorContext usageScenarioBehaviorContext;

		private SimulatedStackframe<Object> resultFrame;

		public B withResultFrame(final SimulatedStackframe<Object> resultFrame) {
			this.resultFrame = resultFrame;
			return this.actualBuilder();
		}

		public B withScenario(final UsageScenario scenario) {
			this.scenario = scenario;
			return this.actualBuilder();
		}

		public B withCurrentAction(final AbstractUserAction currentAction) {
			this.currentAction = currentAction;
			return this.actualBuilder();
		}

		public B withUser(final User user) {
			this.user = user;
			return this.actualBuilder();
		}

		public B withCurrentUsageRun(final int currentUsageRun) {
			this.currentUsageRun = currentUsageRun;
			return this.actualBuilder();
		}

		public B withUsageScenarioBehaviorContext(final UsageScenarioBehaviorContext usageScenarioBehaviorContext) {
			this.usageScenarioBehaviorContext = usageScenarioBehaviorContext;
			return this.actualBuilder();
		}

		@SuppressWarnings("unchecked")
		protected B actualBuilder() {
			return (B) this;
		}

		public abstract T build();
	}

}
