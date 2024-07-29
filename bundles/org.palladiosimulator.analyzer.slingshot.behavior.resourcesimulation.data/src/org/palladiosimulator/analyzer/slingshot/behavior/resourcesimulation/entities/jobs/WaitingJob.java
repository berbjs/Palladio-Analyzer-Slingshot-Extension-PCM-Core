package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs;

import javax.annotation.processing.Generated;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;
import org.palladiosimulator.pcm.repository.PassiveResource;

public final class WaitingJob {

	private final ResourceDemandRequest request;

	private final PassiveResource passiveResource;

	private final long demand;

	@Generated("SparkTools")
	private WaitingJob(final Builder builder) {
		this.request = builder.request;
		this.passiveResource = builder.passiveResource;
		this.demand = builder.demand;
	}

	/**
	 * @return the request
	 */
	public ResourceDemandRequest getRequest() {
		return this.request;
	}

	/**
	 * @return the passiveResource
	 */
	public PassiveResource getPassiveResource() {
		return this.passiveResource;
	}

	/**
	 * @return the demand
	 */
	public long getDemand() {
		return this.demand;
	}

	/**
	 * Creates builder to build {@link WaitingJob}.
	 * 
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link WaitingJob}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private ResourceDemandRequest request;
		private PassiveResource passiveResource;
		private long demand;

		private Builder() {
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
		 * Builder method for passiveResource parameter.
		 * 
		 * @param passiveResource field to set
		 * @return builder
		 */
		public Builder withPassiveResource(final PassiveResource passiveResource) {
			this.passiveResource = passiveResource;
			return this;
		}

		/**
		 * Builder method for demand parameter.
		 * 
		 * @param demand field to set
		 * @return builder
		 */
		public Builder withDemand(final long demand) {
			this.demand = demand;
			return this;
		}

		/**
		 * Builder method of the builder.
		 * 
		 * @return built class
		 */
		public WaitingJob build() {
			return new WaitingJob(this);
		}
	}

}
