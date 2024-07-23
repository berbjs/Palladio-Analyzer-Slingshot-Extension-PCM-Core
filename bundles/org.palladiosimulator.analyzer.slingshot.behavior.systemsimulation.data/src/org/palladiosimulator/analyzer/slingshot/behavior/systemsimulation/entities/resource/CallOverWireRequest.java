package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource;

import java.util.Optional;
import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.GeneralEntryRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.Signature;

import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStackframe;

/**
 * This request is used for calls over the wire and contains all the information
 * needed to make a call to the destination.
 * 
 * This can also be a reply to another request if {@link #getReplyTo()} is not
 * empty.
 * 
 * @author Julijan Katic
 */
public final class CallOverWireRequest {

	private final String id;

	/** The information of where the call is coming from. */
	private final AssemblyContext from;

	/** Tells where the target component is with the target interface */
	private final AssemblyContext to;

	/** The signature at the target interface */
	private final Signature signature;

	/** The user who made the request */
	private final User user;

	/** The request to enter another SEFF component */
	private final GeneralEntryRequest entryRequest;

	/** List of variables to consider for calculating the bytesize */
	private final SimulatedStackframe<Object> variablesToConsider;

	/** The origin request this is responding to */
	private final Optional<CallOverWireRequest> replyTo;

	public CallOverWireRequest(final Builder builder) {
		this.from = builder.from;
		this.to = builder.to;
		this.signature = builder.signature;
		this.user = builder.user;
		this.id = UUID.randomUUID().toString();
		this.entryRequest = builder.entryRequest;
		this.replyTo = Optional.ofNullable(builder.replyTo);
		this.variablesToConsider = builder.variablesToConsider;
	}

	public String getId() {
		return id;
	}

	public SimulatedStackframe<Object> getVariablesToConsider() {
		return this.variablesToConsider;
	}

	public AssemblyContext getFrom() {
		return from;
	}

	public AssemblyContext getTo() {
		return to;
	}

	public Signature getSignature() {
		return signature;
	}

	public User getUser() {
		return user;
	}

	public GeneralEntryRequest getEntryRequest() {
		return entryRequest;
	}

	public Optional<CallOverWireRequest> getReplyTo() {
		return this.replyTo;
	}
	
	/**
	 * This creates a new response request such that the returned instance's
	 * {@link #getReplyTo()} is not empty, and switches the stackframe to consider
	 * for the bytesize calculation.
	 * 
	 * @param returnStackframe The stackframe to consider for the new bytesize
	 *                         calculation.
	 * @return A response request to this one.
	 */
	public CallOverWireRequest createReplyRequest(final SimulatedStackframe<Object> returnStackframe) {
		return builder()
				.from(this.from)
				.to(this.to)
				.entryRequest(this.entryRequest)
				.signature(this.signature)
				.user(this.user)
				.replyTo(this)
				.variablesToConsider(returnStackframe)
				.build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private AssemblyContext from;
		private AssemblyContext to;
		private Signature signature;
		private User user;
		private GeneralEntryRequest entryRequest;
		private CallOverWireRequest replyTo;
		private SimulatedStackframe<Object> variablesToConsider;

		private Builder() {
		}

		public Builder from(final AssemblyContext from) {
			this.from = from;
			return this;
		}

		public Builder to(final AssemblyContext to) {
			this.to = to;
			return this;
		}

		public Builder signature(final Signature signature) {
			this.signature = signature;
			return this;
		}

		public Builder user(final User user) {
			this.user = user;
			return this;
		}

		public Builder entryRequest(final GeneralEntryRequest entryRequest) {
			this.entryRequest = entryRequest;
			return this;
		}

		public Builder replyTo(final CallOverWireRequest replyTo) {
			this.replyTo = replyTo;
			return this;
		}

		public Builder variablesToConsider(final SimulatedStackframe<Object> variablesToConsider) {
			this.variablesToConsider = variablesToConsider;
			return this;
		}

		public CallOverWireRequest build() {
			return new CallOverWireRequest(this);
		}
	}
}
