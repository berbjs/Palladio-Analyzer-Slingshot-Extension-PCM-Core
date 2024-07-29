package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior;

import java.util.Optional;

import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;

/**
 * This represents the root, upper-most scenario where no parent or "next action
 * after this scenario" is present. This is, for example, the scenario behavior
 * that is specified within the usage scenario that is in turn specified in the
 * usage model when creating such model in PCM.
 * <p>
 * It is not possible to repeat the scenario using this class. Instead, when the
 * user is re-entering the system, a new context should be created to have a
 * "clear" start again. Therefore, {@link #mustRepeatScenario()} will always
 * return {@code false} as soon as this scenario has been started.
 * 
 * @author Julijan Katic
 * @version 1.0
 */
public final class RootScenarioContext extends UsageScenarioBehaviorContext {

	private boolean startOnce = true;

	/**
	 * Convenience constructor to build this object without a builder. The
	 * {@code nextAction} and {@code parent} will be set to empty optionals. All
	 * parameter must not be {@code null}.
	 * 
	 * @param scenarioBehavior The scenario behavior that this context should hold.
	 */
	public RootScenarioContext(final ScenarioBehaviour scenarioBehavior) {
		this(builder()
				.withNextAction(Optional.empty())
				.withParent(Optional.empty())
				.withScenarioBehavior(scenarioBehavior));
	}

	public RootScenarioContext(final Builder builder) {
		super(builder);
	}

	@Override
	public boolean mustRepeatScenario() {
		return this.startOnce;
	}

	@Override
	public AbstractUserAction startScenario() {
		final AbstractUserAction userAction = super.startScenario();
		this.startOnce = false;
		return userAction;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder extends BaseBuilder<RootScenarioContext, Builder> {

		@Override
		public RootScenarioContext build() {
			return new RootScenarioContext(this);
		}

	}
}
