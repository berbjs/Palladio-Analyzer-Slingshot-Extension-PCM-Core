package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource;

import static org.palladiosimulator.analyzer.slingshot.common.utils.Logic.implies;

import java.util.Optional;

import javax.annotation.processing.Generated;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.PassiveResource;
import org.palladiosimulator.pcm.seff.seff_performance.ParametricResourceDemand;

import com.google.common.base.Preconditions;

/**
 * The entity holding all information about the resource that is requested.
 * <p>
 * The requested resource can be either an active or a passive resource. If it
 * is a passive resource, then {@link #getPassiveResource()} must be present.
 * <p>
 * As with each entity, no field must be {@code null}. However, optional fields
 * are explicitly declared with {@link Optional}.
 *
 * @author Julijan Katic
 */
public final class ResourceDemandRequest {

	/**
	 * Enum describing whether the resource that is requested is either
	 * {@code ACTIVE} or {@code PASSIVE}. This information is important insofar that
	 * the handling or calculation might be different.
	 */
	public enum ResourceType {
		/** Specifies that the resource is ACTIVE, such as CPU, Ethernet, etc. */
		ACTIVE,

		/**
		 * Specifies that the resource is PASSIVE, such as semaphores, database
		 * connections, etc.
		 */
		PASSIVE
	}

	/** The assembly context from which the request origins. */
	private final AssemblyContext assemblyContext;

	/** The interpretation context from which the request origins. */
	private final SEFFInterpretationContext seffInterpretationContext;

	/** The resource demand of the resource. */
	private final ParametricResourceDemand parametricResourceDemand;

	/** The type of the resource. */
	private final ResourceType resourceType;

	/**
	 * The passive resource itself, if this request is meant for a passive resource.
	 */
	private final Optional<PassiveResource> passiveResource;

	/**
	 * Creates the request. If this is a passive resource, but no passive resource
	 * is specified, then an exception is thrown.
	 *
	 * @param builder
	 */
	@Generated("SparkTools")
	private ResourceDemandRequest(final Builder builder) {
		Preconditions.checkArgument(
				implies(builder.resourceType == ResourceType.PASSIVE, builder.passiveResource.isPresent()),
				"The passive resource must be supplied in this request in order for it to work."
						+ "(ResourceDemandRequest is specified as a ResourceType.PASSIVE, but no passive resource was supplied)");
		this.assemblyContext = builder.assemblyContext;
		this.seffInterpretationContext = builder.seffInterpretationContext;
		this.parametricResourceDemand = builder.parametricResourceDemand;
		this.resourceType = builder.resourceType;
		this.passiveResource = builder.passiveResource;
	}

	/**
	 * Returns the assembly context from which the request origins.
	 *
	 * @return the assemblyContext
	 */
	public AssemblyContext getAssemblyContext() {
		return this.assemblyContext;
	}

	/**
	 * Returns the interpretation context from which the request origins.
	 *
	 * @return the seffInterpretationContext
	 */
	public SEFFInterpretationContext getSeffInterpretationContext() {
		return this.seffInterpretationContext;
	}

	/**
	 * Returns the parametric resource demand that specifies how much resource is
	 * needed.
	 *
	 * @return the parametricResourceDemand
	 */
	public ParametricResourceDemand getParametricResourceDemand() {
		return this.parametricResourceDemand;
	}

	/**
	 * Returns the type of the resource. If not already set by the builder, it
	 * typically defaults to {@link ResourceType#ACTIVE}.
	 *
	 * @return The resource type of the resource.
	 */
	public ResourceType getResourceType() {
		return this.resourceType;
	}

	/**
	 * Short-cut method that directly returns the user by using the interpretation
	 * context.
	 *
	 * @return The user of the interpretation context that requests a resource.
	 */
	public User getUser() {
		return this.seffInterpretationContext.getRequestProcessingContext().getUser();
	}

	/**
	 * Returns the passive resource itself that is being requested. This will return
	 * an empty optional if the resource is actually {@link ResourceType#ACTIVE}.
	 *
	 * @return
	 */
	public Optional<PassiveResource> getPassiveResource() {
		return this.passiveResource;
	}

	/**
	 * Creates builder to build {@link ResourceDemandRequest}.
	 *
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link ResourceDemandRequest}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private AssemblyContext assemblyContext;
		private SEFFInterpretationContext seffInterpretationContext;
		private ParametricResourceDemand parametricResourceDemand;
		private ResourceType resourceType = ResourceType.ACTIVE;
		private Optional<PassiveResource> passiveResource = Optional.empty();

		private Builder() {
		}

		public Builder withAssemblyContext(final AssemblyContext assemblyContext) {
			this.assemblyContext = builderNonNull(assemblyContext);
			return this;
		}

		public Builder withSeffInterpretationContext(final SEFFInterpretationContext seffInterpretationContext) {
			this.seffInterpretationContext = builderNonNull(seffInterpretationContext);
			return this;
		}

		public Builder withParametricResourceDemand(final ParametricResourceDemand parametricResourceDemand) {
			this.parametricResourceDemand = builderNonNull(parametricResourceDemand);
			return this;
		}

		public Builder withResourceType(final ResourceType resourceType) {
			this.resourceType = builderNonNull(resourceType);
			return this;
		}

		public Builder withPassiveResource(final PassiveResource passiveResource) {
			if (passiveResource == null) {
				this.passiveResource = Optional.empty();
			} else {
				this.passiveResource = Optional.of(passiveResource);
			}
			return this;
		}

		public Builder withPassiveResource(final Optional<PassiveResource> passiveResource) {
			this.passiveResource = builderNonNull(passiveResource);
			return this;
		}

		private static <T> T builderNonNull(final T reference) {
			return Preconditions.checkNotNull(reference, "The builder does not allow null values.");
		}

		public ResourceDemandRequest build() {
			return new ResourceDemandRequest(this);
		}
	}

}
