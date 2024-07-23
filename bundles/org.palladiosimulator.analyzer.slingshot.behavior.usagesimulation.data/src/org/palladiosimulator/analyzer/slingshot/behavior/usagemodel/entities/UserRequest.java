package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities;

import javax.annotation.processing.Generated;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;

/**
 * A UserRequest defines the interface needed for the creation of UserRequests.
 * It is compound of the User, the PCM OperationProvidedRole and the
 * OperationSignature.
 *
 * @author Julijan Katic, Sarah Stieß
 */
public final class UserRequest {

	private final User user;
	private final OperationProvidedRole operationProvidedRole;
	private final OperationSignature operationSignature;
	private final EList<VariableUsage> variableUsages;
	private final EList<VariableUsage> outVariableUsages;

	@Generated("SparkTools")
	private UserRequest(final Builder builder) {
		this.user = builder.user;
		this.operationProvidedRole = builder.operationProvidedRole;
		this.operationSignature = builder.operationSignature;
		this.variableUsages = builder.variableUsages;
		this.outVariableUsages = builder.outVariableUsages;
	}

	public User getUser() {
		return user;
	}

	public OperationProvidedRole getOperationProvidedRole() {
		return operationProvidedRole;
	}

	public OperationSignature getOperationSignature() {
		return operationSignature;
	}

	public EList<VariableUsage> getVariableUsages() {
		return variableUsages;
	}

	public EList<VariableUsage> getOutVariableUsages() {
		return outVariableUsages;
	}

	/**
	 * Creates builder to build {@link UserRequest}.
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link UserRequest}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private User user;
		private OperationProvidedRole operationProvidedRole;
		private OperationSignature operationSignature;
		private EList<VariableUsage> variableUsages;
		private EList<VariableUsage> outVariableUsages;

		private Builder() {
		}

		public Builder withUser(final User user) {
			this.user = user;
			return this;
		}

		public Builder withOperationProvidedRole(final OperationProvidedRole operationProvidedRole) {
			this.operationProvidedRole = operationProvidedRole;
			return this;
		}

		public Builder withOperationSignature(final OperationSignature operationSignature) {
			this.operationSignature = operationSignature;
			return this;
		}

		public Builder withVariableUsages(final EList<VariableUsage> variableUsages) {
			this.variableUsages = variableUsages;
			return this;
		}

		public Builder withOutVariableUsages(final EList<VariableUsage> outVariableUsages) {
			this.outVariableUsages = outVariableUsages;
			return this;
		}

		public UserRequest build() {
			return new UserRequest(this);
		}
	}

}
