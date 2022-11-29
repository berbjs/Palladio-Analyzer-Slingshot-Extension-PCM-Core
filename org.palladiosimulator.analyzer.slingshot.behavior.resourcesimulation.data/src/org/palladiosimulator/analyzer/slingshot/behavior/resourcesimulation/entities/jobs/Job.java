package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs;

import javax.annotation.processing.Generated;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;

import de.uka.ipd.sdq.probfunction.math.util.MathTools;

/**
 * A {@link Job} represents an active resource job that either has to be
 * processed, is being processed or was already processed by the simulator.
 * <p>
 * Two jobs are considered equal if their respective {@link #getId()}s are
 * equal.
 * <p>
 * A job is considered "higher" than another job if it has a higher demand.
 * 
 * @author Julijan Katic
 */
public class Job {

	/** The unique id of the job */
	private final String id;

	/** The current demand of this job */
	private double demand;

	/** The processing resource type that is being requested */
	private final ProcessingResourceType processingResourceType;

	/** The allocation context where the resource resides. */
	private final AllocationContext allocationContext;

	/** Keeps track of the resource demand. */
	private final ResourceDemandRequest request;

	@Generated("SparkTools")
	private Job(final Builder builder) {
		this.id = builder.id;
		this.demand = builder.demand;
		this.processingResourceType = builder.processingResourceType;
		this.allocationContext = builder.allocationContext;
		this.request = builder.request;
	}

	/**
	 * Returns the unique identifier of the job. For jobs with the same id, they are
	 * considered to be the same job.
	 * 
	 * @return the id of this job.
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Returns the demand that the resource still needs to process.
	 * 
	 * @return
	 */
	public double getDemand() {
		return this.demand;
	}

	public ProcessingResourceType getProcessingResourceType() {
		return EcoreUtil.copy(this.processingResourceType);
	}

	public ResourceDemandRequest getRequest() {
		return this.request;
	}

	/**
	 * Updates the job's demand to a new demand.
	 * 
	 * @param newDemand The non-negative new demand that needs to be set.
	 */
	public void updateDemand(final double newDemand) {
		this.demand = newDemand;
	}

	/**
	 * Returns whether the job has finished yet. The job is considered finished when
	 * there is no demand left, i.e. {@code this.getDemand() == 0}.
	 * 
	 * @return true iff there is no demand left anymore.
	 */
	public boolean isFinished() {
		return MathTools.equalsDouble(this.demand, 0);
	}

	/**
	 * @return the allocationContext
	 */
	public AllocationContext getAllocationContext() {
		return this.allocationContext;
	}

	/**
	 * Returns the hash code of {@link #getId()}.
	 * 
	 * @generated
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	/**
	 * Checks if two Jobs have the same {@link #getId()}.
	 * 
	 * @generated
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Job other = (Job) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return String.format("Job[%s]: %f (Demand)", this.id, this.demand);
	}

	/**
	 * Creates builder to build {@link Job}.
	 * 
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link Job}.
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
		public Job build() {
			return new Job(this);
		}
	}
}
