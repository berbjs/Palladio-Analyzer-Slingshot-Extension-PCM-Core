package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities;

import javax.annotation.processing.Generated;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserContextEntityHolder;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.repository.Signature;

/**
 * A general entry request is a more general request for a resource demanding
 * service effect specification entry. Unlike {@link User}, this
 * holds the assembly context and the required role as this form of request is
 * typically done by another RDSeff itself.
 * 
 * @author Julijan Katic
 */
public final class GeneralEntryRequest extends UserContextEntityHolder {

	/** Specifies which on with required role the service is called. */
	private final RequiredRole requiredRole;

	/** Specifies which service is called */
	private final Signature signature;

	/** The input variables for the service call. */
	private final EList<VariableUsage> inputVariableUsages;
	
	/** The interpreter from which the request originates. */
	private final SEFFInterpretationContext requestFrom;

	@Generated("SparkTools")
	private GeneralEntryRequest(final Builder builder) {
		super(builder.user);
		this.requiredRole = builder.requiredRole;
		this.signature = builder.signature;
		this.inputVariableUsages = builder.inputVariableUsages;
		this.requestFrom = builder.requestFrom;
	}

	/**
	 * Instantiates a GeneralEntryRequest.
	 * 
	 * @param user                From the super class: The user on which the
	 *                            service is called from.
	 * @param requiredRole        The required role to call.
	 * @param signature           The signature of the call.
	 * @param inputVariableUsages The input variables for the call.
	 */
	public GeneralEntryRequest(final User user, final RequiredRole requiredRole, final Signature signature,
	        final EList<VariableUsage> inputVariableUsages, final SEFFInterpretationContext requestFrom) {
		super(user);
		this.requiredRole = requiredRole;
		this.signature = signature;
		this.inputVariableUsages = inputVariableUsages;
		this.requestFrom = requestFrom;
	}
	
	

	/**
	 * Returns the required role where the service is called.
	 * 
	 * @return the required role.
	 */
	public RequiredRole getRequiredRole() {
		return this.requiredRole;
	}

	/**
	 * Returns the signature of the service. It should be present on the interface
	 * from the required role.
	 * 
	 * @return the signature of the call.
	 */
	public Signature getSignature() {
		return this.signature;
	}

	/**
	 * Returns the list of the input variables for the call.
	 * 
	 * @return the list of input variables.
	 */
	public EList<VariableUsage> getInputVariableUsages() {
		return this.inputVariableUsages;
	}

	public SEFFInterpretationContext getRequestFrom() {
		return this.requestFrom;
	}



	/**
	 * Creates builder to build {@link GeneralEntryRequest}.
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link GeneralEntryRequest}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private User user;
		private RequiredRole requiredRole;
		private Signature signature;
		private EList<VariableUsage> inputVariableUsages;
		private SEFFInterpretationContext requestFrom;

		private Builder() {
		}

		public Builder withUser(final User user) {
			this.user = user;
			return this;
		}

		public Builder withRequiredRole(final RequiredRole requiredRole) {
			this.requiredRole = requiredRole;
			return this;
		}

		public Builder withSignature(final Signature signature) {
			this.signature = signature;
			return this;
		}

		public Builder withInputVariableUsages(final EList<VariableUsage> inputVariableUsages) {
			this.inputVariableUsages = inputVariableUsages;
			return this;
		}

		public Builder withRequestFrom(final SEFFInterpretationContext requestFrom) {
			this.requestFrom = requestFrom;
			return this;
		}

		public GeneralEntryRequest build() {
			return new GeneralEntryRequest(this);
		}
	}

}