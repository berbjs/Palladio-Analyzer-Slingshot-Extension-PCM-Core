package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior;

import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;

import com.google.common.base.Preconditions;

/**
 * This class is used for loops with inner scenario behaviors. It contains a maximal loop count,
 * that is, how many iterations are permitted, and the current progression of that iteration.
 * <p>
 * The scenario must be repeated as long as the progression has not reached the maximal loop count
 * yet. The scenario shouldn't be repeated anymore as soon as the progression has reached that loop
 * count.
 * <p>
 * When the scenario is started, the progression is incremented by one automatically. The
 * post-condition of maximalLoopCount executions of the specified scenario holds only if the
 * {@link #startScenario()} is called before interpreting the scenario itself. The
 * {@link #startScenario()} returns the StartAction of the ScenarioBehavior. It is discouraged to
 * retrieve the StartAction and interpret the scenario without starting the scenario. 
 * <p>
 * Because this is an inner scenario behavior, both the {@code nextAction} and the {@code parent}
 * must be present.
 * 
 * @author Julijan Katic, Floriment Klinaku
 */
public final class LoopScenarioBehaviorContext extends UsageScenarioBehaviorContext {

	private final int maximalLoopCount;
	private int progression;

	/**
	 * Constructs the LoopScenarioBehaviorContext using the {@link Builder} object.
	 * 
	 * @param builder
	 */
	public LoopScenarioBehaviorContext(final Builder builder) {
		super(builder);
		Preconditions.checkArgument(this.getNextAction().isPresent(), "The next action must be present");
		Preconditions
				.checkArgument(builder.initialLoopCount < builder.maximalLoopCount && builder.initialLoopCount >= 0);

		this.maximalLoopCount = builder.maximalLoopCount;
		this.progression = builder.initialLoopCount;
	}

	@Override
	public boolean mustRepeatScenario() {
		return this.progression < this.maximalLoopCount;
	}

	@Override
	public AbstractUserAction startScenario() {
		final AbstractUserAction userAction = super.startScenario();
		this.progression++;
		return userAction;
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builds this loop context. The maximal loop count is initially {@code -1}. The
	 * initial loop count is initially {@code 0}. According to
	 * {@link #LoopScenarioBehaviorContext()}, the maximal loop count should be
	 * larger than the inital loop count.
	 * 
	 * @author Julijan Katic
	 */
	public static final class Builder extends BaseBuilder<LoopScenarioBehaviorContext, Builder> {

		private int maximalLoopCount;
		private int initialLoopCount;

		private Builder() {
			this.maximalLoopCount = -1;
			this.initialLoopCount = 0;
		}

		public Builder withMaximalLoopCount(final int maximalLoopCount) {
			this.maximalLoopCount = maximalLoopCount;
			return this;
		}

		public Builder withInitialLoopCount(final int initialLoopCount) {
			this.initialLoopCount = initialLoopCount;
			return this;
		}

		@Override
		public LoopScenarioBehaviorContext build() {
			return new LoopScenarioBehaviorContext(this);
		}

	}
}
