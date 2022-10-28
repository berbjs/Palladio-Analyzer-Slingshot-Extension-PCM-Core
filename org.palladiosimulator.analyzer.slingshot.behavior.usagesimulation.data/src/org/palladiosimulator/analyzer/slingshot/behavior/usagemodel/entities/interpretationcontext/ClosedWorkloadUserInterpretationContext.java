package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext;

import javax.annotation.processing.Generated;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.ThinkTime;

/**
 * The interpretation context for <strong>Closed</strong> workload users. Closed
 * workload users have a {@link ThinkTime} that is used to let a user re-enter
 * the system after they finished interpretation of the complete usage scenario.
 * <p>
 * This is in opposite of {@link OpenWorkloadUserInterpretationContext}.
 * 
 * @author Julijan Katic
 * @version 1.0
 */
public final class ClosedWorkloadUserInterpretationContext extends UserInterpretationContext {

	private final ThinkTime thinkTime;

	@Generated("SparkTools")
	private ClosedWorkloadUserInterpretationContext(final Builder builder) {
		super(builder);
		this.thinkTime = builder.thinkTime;
	}

	/**
	 * Returns the think time, after which the user re-enters the system.
	 * 
	 * @return non-{@code null} instance of the proxy think time.
	 */
	public ThinkTime getThinkTime() {
		return this.thinkTime;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Builder update() {
		final Builder builder = this.updateWithBuilder(builder());
		return builder.withThinkTime(this.thinkTime);
	}

	/**
	 * Creates builder to build {@link ClosedWorkloadUserInterpretationContext}.
	 * 
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link ClosedWorkloadUserInterpretationContext}.
	 */
	@Generated("SparkTools")
	public static final class Builder extends BaseBuilder<ClosedWorkloadUserInterpretationContext, Builder> {
		private ThinkTime thinkTime;

		private Builder() {
		}

		public Builder withThinkTime(final ThinkTime thinkTime) {
			this.thinkTime = thinkTime;
			return this;
		}

		@Override
		public ClosedWorkloadUserInterpretationContext build() {
			return new ClosedWorkloadUserInterpretationContext(this);
		}
	}

}
