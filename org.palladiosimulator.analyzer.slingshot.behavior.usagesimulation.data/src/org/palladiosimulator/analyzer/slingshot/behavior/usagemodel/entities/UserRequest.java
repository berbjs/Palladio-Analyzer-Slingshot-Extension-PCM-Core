package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import javax.annotation.processing.Generated;

/**
 * A UserRequest defines the interface needed for the creation of UserRequests.
 * It is compound of the User, the PCM OperationProvidedRole and the
 * OperationSignature.
 * 
 * @author Julijan Katic
 */
public final class UserRequest {

	private final User user;
	private final OperationProvidedRole operationProvidedRole;
	private final OperationSignature operationSignature;
	private final EList<VariableUsage> variableUsages;

	@Generated("SparkTools")
	private UserRequest(Builder builder) {
		this.user = builder.user;
		this.operationProvidedRole = builder.operationProvidedRole;
		this.operationSignature = builder.operationSignature;
		this.variableUsages = builder.variableUsages;
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

		private Builder() {
		}

		public Builder withUser(User user) {
			this.user = user;
			return this;
		}

		public Builder withOperationProvidedRole(OperationProvidedRole operationProvidedRole) {
			this.operationProvidedRole = operationProvidedRole;
			return this;
		}

		public Builder withOperationSignature(OperationSignature operationSignature) {
			this.operationSignature = operationSignature;
			return this;
		}

		public Builder withVariableUsages(EList<VariableUsage> variableUsages) {
			this.variableUsages = variableUsages;
			return this;
		}

		public UserRequest build() {
			return new UserRequest(this);
		}
	}

}
