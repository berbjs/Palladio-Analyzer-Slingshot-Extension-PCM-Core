package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs;

import java.util.Objects;

public abstract class Job {

	/** The unique id of the job */
	private final String id;

	/** The current demand of this job */
	private double demand;

	public Job(final String id, final double demand) {
		super();
		this.id = id;
		this.demand = demand;
	}

	/**
	 * Returns the demand that the resource still needs to process.
	 *
	 * @return
	 */
	public double getDemand() {
		return demand;
	}

	/**
	 * Updates the job's demand to a new demand.
	 *
	 * @param newDemand The non-negative new demand that needs to be set.
	 */
	public void updateDemand(final double demand) {
		this.demand = demand;
	}

	/**
	 * Returns the unique identifier of the job. For jobs with the same id, they are
	 * considered to be the same job.
	 *
	 * @return the id of this job.
	 */
	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Job other = (Job) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Job [id=" + id + ", demand=" + demand + "]";
	}

}
