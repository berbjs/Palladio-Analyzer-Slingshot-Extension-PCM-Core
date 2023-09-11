package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.processing.Generated;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;

/**
 * A {@link ActiveJob} represents an active resource job that either has to be
 * processed, is being processed or was already processed by the simulator.
 * <p>
 * Two jobs are considered equal if their respective {@link #getId()}s are
 * equal.
 * <p>
 * A job is considered "higher" than another job if it has a higher demand.
 *
 * @author Julijan Katic
 */
public class ActiveJob extends Job {

	/** The processing resource type that is being requested */
	private final ProcessingResourceType processingResourceType;

	/** The allocation context where the resource resides. */
	private final AllocationContext allocationContext;

	/** Keeps track of the resource demand. */
	private final ResourceDemandRequest request;

	@Generated("SparkTools")
	private ActiveJob(final Builder builder) {
		super(builder.id, builder.demand);
		this.processingResourceType = builder.processingResourceType;
		this.allocationContext = builder.allocationContext;
		this.request = builder.request;
	}

	public ProcessingResourceType getProcessingResourceType() {
		return EcoreUtil.copy(this.processingResourceType);
	}

	public ResourceDemandRequest getRequest() {
		return this.request;
	}

	/**
	 * @return the allocationContext
	 */
	public AllocationContext getAllocationContext() {
		return this.allocationContext;
	}

	/**
	 * Returns the processing resource specification of the job's resource container
	 * that also matches the job's processing resource type.
	 *
	 * @return the processingResourceSpecification
	 */
	public ProcessingResourceSpecification getProcessingResourceSpecification() {
		final List<ProcessingResourceSpecification> matches = this.allocationContext
				.getResourceContainer_AllocationContext().getActiveResourceSpecifications_ResourceContainer().stream()
				.filter(
				spec -> spec.getActiveResourceType_ActiveResourceSpecification().equals(this.processingResourceType))
				.collect(Collectors.toList());

		if (matches.size() != 1) {
			throw new IllegalArgumentException(
					String.format("Wrong number of matching ProcessingResourceSpecifications, expected 1 but found %d",
							matches.size()));
		}

		return matches.get(0);
	}

	/**
	 * Creates builder to build {@link ActiveJob}.
	 *
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link ActiveJob}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private String id;
		private double demand;
		private ProcessingResourceType processingResourceType;
		private AllocationContext allocationContext;
		private ResourceDemandRequest request;

		private Builder() {
		}

		/**
		 * Builder method for id parameter.
		 *
		 * @param id field to set
		 * @return builder
		 */
		public Builder withId(final String id) {
			this.id = id;
			return this;
		}

		/**
		 * Builder method for demand parameter.
		 *
		 * @param demand field to set
		 * @return builder
		 */
		public Builder withDemand(final double demand) {
			this.demand = demand;
			return this;
		}

		/**
		 * Builder method for processingResourceType parameter.
		 *
		 * @param processingResourceType field to set
		 * @return builder
		 */
		public Builder withProcessingResourceType(final ProcessingResourceType processingResourceType) {
			this.processingResourceType = processingResourceType;
			return this;
		}

		/**
		 * Builder method for allocationContext parameter.
		 *
		 * @param allocationContext field to set
		 * @return builder
		 */
		public Builder withAllocationContext(final AllocationContext allocationContext) {
			this.allocationContext = allocationContext;
			return this;
		}

		/**
		 * Builder method for request parameter.
		 *
		 * @param request field to set
		 * @return builder
		 */
		public Builder withRequest(final ResourceDemandRequest request) {
			this.request = request;
			return this;
		}

		/**
		 * Builder method of the builder.
		 *
		 * @return built class
		 */
		public ActiveJob build() {
			return new ActiveJob(this);
		}
	}
}
