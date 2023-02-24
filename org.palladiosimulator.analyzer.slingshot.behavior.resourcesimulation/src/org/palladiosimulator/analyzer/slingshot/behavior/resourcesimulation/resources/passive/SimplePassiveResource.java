package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.passive;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.WaitingJob;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.IPassiveResource;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.AbstractResource;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.PassiveResourceAcquired;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.PassiveResource;

/**
 * A simple passive resource simulates a semaphore which holds a queue of
 * {@link WaitingJob}s and has a maximum {@code capacity} to grant the resource
 * to multiple jobs parallely. If the maximum capacity has been reached, the job
 * will be added to the queue. As soon as a job releases the resource, the first
 * jobs in the queue will be granted the resource until the capacity has again
 * been reached.
 *
 *
 * @author Julijan Katic
 *
 */
public final class SimplePassiveResource extends AbstractResource implements IPassiveResource {

	/** The FIFO queue of waiting jobs to acquire next. */
	private final Queue<WaitingJob> waitingJobs;

	/** The current amount of capacity that can be used by the next jobs. */
	private long available;

	/**
	 * Constructs a new simple passive resource.
	 *
	 * @param id       The id of this resource.
	 * @param capacity The initial capacity the resource has.
	 */
	public SimplePassiveResource(final PassiveResourceCompoundKey id, final long capacity) {
		super(capacity, id.getPassiveResource().getEntityName(), id);
		this.waitingJobs = new ArrayDeque<>();
		this.available = capacity;
	}

	@Override
	public void clearJobs() {
		this.waitingJobs.clear();
	}

	/**
	 * Acquires the resource directly if the demand is available, or adds the job to
	 * the queue if the capacity is not sufficient right now. This will result
	 * either in a {@link PassiveResourceAcquired} event if the resource was granted
	 * to the job, or in an empty {@code Result} if not.
	 *
	 * @param waitingJob The job to acquire.
	 * @return Either {@link PassiveResourceAcquired} if granted, or empty.
	 */
	public PassiveResourceAcquired acquire(final WaitingJob waitingJob) {
		/* TODO: Throw exception if demand is higher than capacity. */
		if (this.acquirable(waitingJob)) {
			return grantAccess(waitingJob);
		} else {
			this.waitingJobs.offer(waitingJob);
			return null;
		}
	}

	/**
	 * Releases the resource from the specified job with the demand and the current
	 * available capacity will be increased accordingly. Furthermore, this will
	 * grant the next waiting jobs according until the new capacity has been reached
	 * again. These jobs will hence be deleted from the queue.
	 *
	 * @param waitingJob The job releasing this resource.
	 * @return {@link PassiveResourceAcquired} events for the next jobs waiting in
	 *         the queue to be granted.
	 */
	public Result<PassiveResourceAcquired> release(final WaitingJob waitingJob) {
		this.available += waitingJob.getDemand();

		final Set<PassiveResourceAcquired> events = new HashSet<>();

		WaitingJob nextJob = this.waitingJobs.peek();

		while (nextJob != null && this.acquirable(nextJob)) {
			events.add(this.grantAccess(nextJob));
			nextJob = this.waitingJobs.peek();
		}

		return Result.from(events);
	}

	/**
	 * Checks whether the job can acquire this resource by looking whether the job
	 * would be next in the queue (or if the queue is empty) and the capacity is
	 * sufficient for the specified demand.
	 *
	 * @param waitingJob The job to check whether to be granted.
	 * @return true if the job can acquire this resource.
	 */
	public boolean acquirable(final WaitingJob waitingJob) {
		return (this.waitingJobs.isEmpty() || this.waitingJobs.peek().equals(waitingJob))
				&& waitingJob.getDemand() <= this.available;
	}

	/**
	 * Grants the access to the resource. This will decrease the available capacity
	 * and remove it from the queue if contained.
	 *
	 * @param waitingJob The job to grant access to.
	 * @return The event that can be added to the {@code Result}.
	 */
	private PassiveResourceAcquired grantAccess(final WaitingJob waitingJob) {
		assert this.acquirable(waitingJob);

		this.available -= waitingJob.getDemand();
		this.waitingJobs.remove(waitingJob);

		return new PassiveResourceAcquired(waitingJob.getRequest());
	}

	@Override
	public long getCurrentlyAvailable() {
		return this.available;
	}

	@Override
	public PassiveResource getPCMPassiveResource() {
		return ((PassiveResourceCompoundKey) this.getId()).getPassiveResource();
	}

	@Override
	public AssemblyContext getAssemblyContext() {
		return ((PassiveResourceCompoundKey) this.getId()).getAssemblyContext();
	}
}
