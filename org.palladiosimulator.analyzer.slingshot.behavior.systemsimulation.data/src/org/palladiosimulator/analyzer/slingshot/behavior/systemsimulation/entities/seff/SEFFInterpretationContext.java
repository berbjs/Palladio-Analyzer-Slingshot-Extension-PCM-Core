package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff;

import java.util.Optional;

import javax.annotation.processing.Generated;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.SeffBehaviorContextHolder;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.user.RequestProcessingContext;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;

import com.google.common.base.Preconditions;

/**
 * The SEFFInterpretationContext is used for keeping track of the RDSeff
 * interpretation.
 *
 * @author Julijan Katic, Sarah Stieß
 * @version 1.0
 */
public final class SEFFInterpretationContext {

	/** The context where the interpretation is currently in */
	private final SeffBehaviorContextHolder behaviorContext;

	private final RequestProcessingContext requestProcessingContext;

	private final AssemblyContext assemblyContext;

	private final Optional<SEFFInterpretationContext> calledFrom;
	private final Optional<InfrastructureCallsContext> calledFromInfra;

	@Generated("SparkTools")
	private SEFFInterpretationContext(final Builder builder) {
		this.calledFrom = builder.calledFrom;
		this.behaviorContext = builder.behaviorContext;
		this.requestProcessingContext = builder.requestProcessingContext;
		this.assemblyContext = builder.assemblyContext;
		this.calledFromInfra = builder.calledFromInfra;
	}

	/**
	 * @return the behaviorContext
	 */
	public SeffBehaviorContextHolder getBehaviorContext() {
		return this.behaviorContext;
	}

	/**
	 * @return the requestProcessingContext
	 */
	public RequestProcessingContext getRequestProcessingContext() {
		return this.requestProcessingContext;
	}

	public AssemblyContext getAssemblyContext() {
		return this.assemblyContext;
	}

	public Optional<SEFFInterpretationContext> getCaller() {
		return this.calledFrom;
	}

	/**
	 *
	 * @return the calling
	 */
	public Optional<InfrastructureCallsContext> getInfraCaller() {
		return this.calledFromInfra;
	}

	public Builder update() {
		return builder()
				.withBehaviorContext(this.behaviorContext)
				.withAssemblyContext(this.assemblyContext)
				.withRequestProcessingContext(this.requestProcessingContext);
	}

	/**
	 * Creates builder to build {@link SEFFInterpretationContext}.
	 *
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link SEFFInterpretationContext}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private SeffBehaviorContextHolder behaviorContext;
		private RequestProcessingContext requestProcessingContext;
		private AssemblyContext assemblyContext;
		private Optional<SEFFInterpretationContext> calledFrom = Optional.empty();
		private Optional<InfrastructureCallsContext> calledFromInfra = Optional.empty();

		private Builder() {
		}

		public Builder withBehaviorContext(final SeffBehaviorContextHolder behaviorContext) {
			this.behaviorContext = builderNonNull(behaviorContext);
			return this;
		}

		public Builder withCaller(final SEFFInterpretationContext calledFrom) {
			if (calledFrom != null) {
				this.calledFrom = Optional.of(calledFrom);
			} else {
				this.calledFrom = Optional.empty();
			}
			return this;
		}

		public Builder withInfraCaller(final InfrastructureCallsContext calledFromInfra) {
			if (calledFromInfra != null) {
				this.calledFromInfra = Optional.of(calledFromInfra);
			} else {
				this.calledFromInfra = Optional.empty();
			}
			return this;
		}

		public Builder withRequestProcessingContext(final RequestProcessingContext requestProcessingContext) {
			this.requestProcessingContext = builderNonNull(requestProcessingContext);
			return this;
		}

		public Builder withAssemblyContext(final AssemblyContext assemblyContext) {
			this.assemblyContext = builderNonNull(assemblyContext);
			return this;
		}

		public Builder withCaller(final Optional<SEFFInterpretationContext> caller) {
			this.calledFrom = builderNonNull(caller);
			return this;
		}

		public Builder withInfraCaller(final Optional<InfrastructureCallsContext> infraCaller) {
			this.calledFromInfra = builderNonNull(infraCaller);
			return this;
		}

		public SEFFInterpretationContext build() {
			return new SEFFInterpretationContext(this);
		}

		private static <T> T builderNonNull(final T reference) {
			return Preconditions.checkNotNull(reference, "Builder does not allow null-references.");
		}
	}

}
