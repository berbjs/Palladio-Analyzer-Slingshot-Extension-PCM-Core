package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext;

import javax.annotation.processing.Generated;

import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

/**
 * This interpretation context holds the usage scenario that is going to be
 * interpreted. It also consists of multiple methods for further information
 * about the scenario.
 * 
 * @author Julijan Katic
 * @version 1.0
 */
public final class UsageScenarioInterpretationContext {

	/** The corresponding usage scenario to be interpreted. */
	private final UsageScenario scenario;

	@Generated("SparkTools")
	private UsageScenarioInterpretationContext(final Builder builder) {
		this.scenario = builder.scenario;
	}

	/**
	 * Returns whether the specified workflow of the usage scenario is a
	 * {@link ClosedWorkload}.
	 * 
	 * @return true if the workflow is a ClosedWorkload.
	 * @see #isOpenWorkload()
	 */
	public boolean isClosedWorkload() {
		return this.scenario.getWorkload_UsageScenario() instanceof ClosedWorkload;
	}

	/**
	 * Returns whether the specified workflow of the usage scenario is a
	 * {@link OpenWorkload}
	 * 
	 * @return true if the workflow is a OpenWorkload.
	 * @see #isClosedWorkload()
	 */
	public boolean isOpenWorkload() {
		return this.scenario.getWorkload_UsageScenario() instanceof OpenWorkload;
	}

	public UsageScenario getScenario() {
		return this.scenario;
	}

	/**
	 * Creates builder to build {@link UsageScenarioInterpretationContext}.
	 * 
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link UsageScenarioInterpretationContext}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private UsageScenario scenario;

		private Builder() {
		}

		public Builder withScenario(final UsageScenario scenario) {
			this.scenario = scenario;
			return this;
		}

		public UsageScenarioInterpretationContext build() {
			return new UsageScenarioInterpretationContext(this);
		}
	}

}
