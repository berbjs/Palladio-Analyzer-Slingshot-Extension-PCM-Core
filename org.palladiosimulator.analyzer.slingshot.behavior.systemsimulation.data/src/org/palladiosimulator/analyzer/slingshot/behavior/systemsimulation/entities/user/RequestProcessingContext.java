package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.user;

import javax.annotation.processing.Generated;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.ProvidedRole;

/**
 * The RequestProcessingContext is used in order to hold information about the
 * user as well as SEFF-specific interpretation information. This can be used in
 * order to tell the Usage simulator that the user has finished (or left) the
 * system.
 * <p>
 * The request specific information are the user itself and the user request.
 * Furthermore, for the system to know which SEFF to look for, this context
 * holds the assembly context as well as the provided role from which the user
 * entered the system. The SEFF specific information is stored in its own class,
 * whose instance is stored here.
 * 
 * @author Julijan Katic
 * @version 1.0
 */
public final class RequestProcessingContext {

	private final UserRequest userRequest;
	private final User user;
	private final ProvidedRole providedRole;
	private final AssemblyContext assemblyContext;
	private final UserInterpretationContext userInterpretationContext;

	@Generated("SparkTools")
	private RequestProcessingContext(final Builder builder) {
		this.userRequest = builder.userRequest;
		this.user = builder.user;
		this.providedRole = builder.providedRole;
		this.assemblyContext = builder.assemblyContext;
		this.userInterpretationContext = builder.userInterpretationContext;
	}
	
	

	/**
	 * @return the userRequest
	 */
	public UserRequest getUserRequest() {
		return this.userRequest;
	}



	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}



	/**
	 * @return the providedRole
	 */
	public ProvidedRole getProvidedRole() {
		return this.providedRole;
	}



	/**
	 * @return the assemblyContext
	 */
	public AssemblyContext getAssemblyContext() {
		return this.assemblyContext;
	}

	
	public UserInterpretationContext getUserInterpretationContext() {
		return this.userInterpretationContext;
	}


	/**
	 * Creates builder to build {@link RequestProcessingContext}.
	 * 
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link RequestProcessingContext}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private UserRequest userRequest;
		private User user;
		private ProvidedRole providedRole;
		private AssemblyContext assemblyContext;
		private UserInterpretationContext userInterpretationContext;

		private Builder() {
		}

		public Builder withUserRequest(final UserRequest userRequest) {
			this.userRequest = userRequest;
			return this;
		}

		public Builder withUser(final User user) {
			this.user = user;
			return this;
		}

		public Builder withProvidedRole(final ProvidedRole providedRole) {
			this.providedRole = providedRole;
			return this;
		}

		public Builder withAssemblyContext(final AssemblyContext assemblyContext) {
			this.assemblyContext = assemblyContext;
			return this;
		}

		public RequestProcessingContext build() {
			return new RequestProcessingContext(this);
		}

		public Builder withUserInterpretationContext(final UserInterpretationContext userInterpretationContext) {
			this.userInterpretationContext = userInterpretationContext;
			return this;
		}
	}

}
