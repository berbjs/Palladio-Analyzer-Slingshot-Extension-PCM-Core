package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff;

import java.util.Optional;

import javax.annotation.processing.Generated;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.CallOverWireRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.SeffBehaviorContextHolder;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.user.RequestProcessingContext;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;

import com.google.common.base.Preconditions;

import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStackframe;

/**
 * The SEFFInterpretationContext is used for keeping track of the RDSeff
 * interpertation.
 *
 * Child contexts (i.e. nested SEFFs) always hold the caller
 * ({@code calledFrom}) of their parent context. However, only Root behaviours
 * should return to their callers. Others should return to their parent.
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

	/** The parent context if this is a child context */
	private final Optional<SEFFInterpretationContext> parent;

	private final Optional<CallOverWireRequest> callOverWireRequest;

	/**
	 * The stackframe to hold the result variables of a call. This can be null,
	 * signifying the the result stackframe is in the parent already or the users
	 * current stackframe should be used.
	 */
	private final SimulatedStackframe<Object> resultStackframe;

	@Generated("SparkTools")
	private SEFFInterpretationContext(final Builder builder) {
		this.calledFrom = builder.calledFrom;
		this.behaviorContext = builder.behaviorContext;
		this.requestProcessingContext = builder.requestProcessingContext;
		this.assemblyContext = builder.assemblyContext;
		this.callOverWireRequest = Optional.ofNullable(builder.callOverWireRequest);
		this.parent = Optional.ofNullable(builder.parent);
		this.resultStackframe = builder.resultStackframe;
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

	public Optional<CallOverWireRequest> getCallOverWireRequest() {
		return this.callOverWireRequest;
	}

	/**
	 * Returns the stack frame to which variables can be set. This can either be a
	 * dedicated stack frame or the current stackframe from the user.
	 *
	 * If the result stack frame was set when constructing this context, then this
	 * dedicated stack frame will be returned. Otherwise, the parent's
	 * {@link #getCurrentResultStackframe()} will be returned.
	 *
	 * If the parent's stackframe are also {@code null}, the current user's stack
	 * frame will be returned instead.
	 *
	 * @return A non-{@code null} stackframe object, either dedicated or the current
	 *         user's stack frame.
	 */
	public SimulatedStackframe<Object> getCurrentResultStackframe() {
		if (this.resultStackframe != null) {
			return this.resultStackframe;
		}

		return this.parent.map(pr -> pr.getCurrentResultStackframe())
				.orElseGet(() -> this.requestProcessingContext.getUser().getStack().currentStackFrame());
	}

	public Optional<SEFFInterpretationContext> getParent() {
		return this.parent;
	}

	/**
	 * Creates a child context from this with empty fields, except that
	 * {@link #getParent()} will point to this and
	 * {@link #getRequestProcessingContext()} will stay the same.
	 *
	 * @return A builder for the child context.
	 */
	public Builder createChildContext() {
		return builder().withRequestProcessingContext(requestProcessingContext).withParent(this);
	}

	/**
	 * Creates a child context from this with the same values as this, except that
	 * the result stack frame will be {@code null} since the result stack frame is
	 * already set in the parent.
	 *
	 * @return A builder with pre-filled fields for the child context.
	 */
	public Builder createChildContextPrefilled() {
		return update().withParent(this).withResultStackframe(null);
	}

	public Builder update() {
		return builder()
				.withBehaviorContext(this.behaviorContext)
				.withAssemblyContext(this.assemblyContext)
				.withRequestProcessingContext(this.requestProcessingContext)
				.withCaller(this.calledFrom)
				.withParent(this.parent.orElse(null))
				.withCallOverWireRequest(this.callOverWireRequest.orElse(null))
				.withResultStackframe(getCurrentResultStackframe());
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
		private CallOverWireRequest callOverWireRequest;
		private SEFFInterpretationContext parent;
		private SimulatedStackframe<Object> resultStackframe;

		private Builder() {
		}

		public Builder withParent(final SEFFInterpretationContext parent) {
			this.parent = parent;
			return this;
		}

		public Builder withCallOverWireRequest(final CallOverWireRequest callOverWireRequest) {
			this.callOverWireRequest = callOverWireRequest;
			return this;
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

		public Builder withResultStackframe(final SimulatedStackframe<Object> stackFrame) {
			this.resultStackframe = stackFrame;
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
