package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext;

import javax.annotation.processing.Generated;

import com.google.common.collect.ImmutableList;

/**
 * This class represents the context of a usage model that is needed to be
 * interpreted. It contains an immutable list of
 * {@link UsageScenarioInterpretationContext}.
 * 
 * This class is immutable.
 * 
 * @author Julijan Katic
 */
public final class UsageInterpretationContext {

	private final ImmutableList<UsageScenarioInterpretationContext> usageScenariosContexts;

	@Generated("SparkTools")
	private UsageInterpretationContext(final Builder builder) {
		this.usageScenariosContexts = builder.usageScenariosContexts;
	}

	public ImmutableList<UsageScenarioInterpretationContext> getUsageScenarioContexts() {
		return this.usageScenariosContexts;
	}

	/**
	 * Creates builder to build {@link UsageInterpretationContext}.
	 * 
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link UsageInterpretationContext}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private ImmutableList<UsageScenarioInterpretationContext> usageScenariosContexts;

		private Builder() {
		}

		public Builder withUsageScenariosContexts(
				final ImmutableList<UsageScenarioInterpretationContext> usageScenariosContexts) {
			this.usageScenariosContexts = usageScenariosContexts;
			return this;
		}

		public UsageInterpretationContext build() {
			return new UsageInterpretationContext(this);
		}
	}

}
