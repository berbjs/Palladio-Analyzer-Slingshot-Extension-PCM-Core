package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource;

import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.GeneralEntryRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.Signature;

public final class CallOverWireRequest {

	private final String id;
	private final AssemblyContext from;
	private final AssemblyContext to;
	private final Signature signature;
	private final User user;
	private final GeneralEntryRequest entryRequest;

	public CallOverWireRequest(final Builder builder) {
		this.from = builder.from;
		this.to = builder.to;
		this.signature = builder.signature;
		this.user = builder.user;
		this.id = UUID.randomUUID().toString();
		this.entryRequest = builder.entryRequest;
	}

	public String getId() {
		return id;
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

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private AssemblyContext from;
		private AssemblyContext to;
		private Signature signature;
		private User user;
		private GeneralEntryRequest entryRequest;

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

		public CallOverWireRequest build() {
			return new CallOverWireRequest(this);
		}
	}
}
