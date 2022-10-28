package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active;

import java.util.ArrayDeque;
import java.util.Deque;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.AbstractJobEvent;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

import de.uka.ipd.sdq.probfunction.math.util.MathTools;

/**
 * A FCFSResource handles first jobs first, and then the remaining jobs will be
 * handled (first-come, first-served).
 * 
 * @author Julijan Katic
 */
public class FCFSResource extends AbstractActiveResource {

	/** The FIFO queue of jobs to handle. */
	private final Deque<Job> processes = new ArrayDeque<>();

	/**
	 * The value tracking the simulation time in order to specify the elapsed time
	 * for the jobs.
	 */
	private double internalTimer;

	/**
	 * Constructs a new FCFS resource.
	 * 
	 * @param type     The processor resource type whose id will be this id.
	 * @param name     The name of the resource.
	 * @param capacity The maximum capacity of the resource.
	 */
	public FCFSResource(final ActiveResourceCompoundKey type, final String name, final long capacity) {
		super(type, name, capacity);
	}

	/**
	 * Handles the event by adding the newly created job to the queue and updating
	 * its internal timer. Will result in a {@link JobProgressed} event holding the
	 * next job to process.
	 * 
	 * @return {@link JobProgressed} events.
	 */
	@Override
	protected Result process(final JobInitiated jobInitiated) {
		this.updateInternalTimer(jobInitiated.time());
		final Job newJob = jobInitiated.getEntity();

		this.processes.add(newJob);

		if(this.processes.size()!=1) {
			return Result.empty();
		}
		
		return Result.of(this.scheduleNextEvent());
	}

	/**
	 * Handles the job by updating its internal timer and removing the job from the
	 * queue.
	 * 
	 * @return {@link JobFinished} event from that removed job, and
	 *         {@link JobProgressed} from the next job to handle.
	 */
	@Override
	public Result onJobProgressed(final JobProgressed jobProgressed) {
		this.updateInternalTimer(jobProgressed.time());

		final Job job = jobProgressed.getEntity();

		assert MathTools.equalsDouble(0, job.getDemand()) : "Remaining demand (" + job.getDemand() + ") not zero!";

		this.processes.remove(job);
		return Result.of(new JobFinished(job), this.scheduleNextEvent());
	}

	@Override
	public void clearJobs() {
		this.processes.clear();
	}

	/**
	 * Updates the internal timer and the demand of the next job to handle by
	 * subtracting the passed time from the demand.
	 * 
	 * @param simulationTime The new simulation time. Must be greater than the
	 *                       internal time.
	 */
	private void updateInternalTimer(final double simulationTime) {
		final double passedTime = simulationTime - this.internalTimer;

		if (MathTools.less(0, passedTime)) {
			final Job firstJob = this.processes.peek();
			if (firstJob != null) {
				double demand = firstJob.getDemand() - passedTime;
				demand = MathTools.equalsDouble(demand, 0) ? 0.0 : demand; // avoid rounding issues.
				firstJob.updateDemand(demand);
			}
		}

		this.internalTimer = simulationTime;
	}

	/**
	 * Schedules the next {@link JobProgressed} event by returning that event
	 * accompanied with the next job to handle according to the FIFO principle.
	 * <p>
	 * The event will be delayed by the current job's demand.
	 * 
	 * @return The new JobProgressed event if there is any, or {@code null}
	 *         otherwise.
	 */
	private JobProgressed scheduleNextEvent() {
		final Job first = this.processes.peek();

		if (first != null) {
			return new JobProgressed(first, first.getDemand());
		}

		return null;
	}
}
