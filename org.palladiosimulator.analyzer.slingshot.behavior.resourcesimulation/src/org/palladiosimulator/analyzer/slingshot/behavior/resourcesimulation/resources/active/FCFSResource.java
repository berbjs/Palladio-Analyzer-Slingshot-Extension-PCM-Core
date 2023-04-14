package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.ProcessingRate;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.AbstractJobEvent;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;
import de.uka.ipd.sdq.probfunction.math.util.MathTools;

/**
 * A FCFSResource handles first jobs first, and then the remaining jobs will be
 * handled (first-come, first-served).
 *
 * @author Julijan Katic, Floriment Klinaku, Sarah Stiess
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
	public FCFSResource(final ActiveResourceCompoundKey type, final String name, final long capacity, final ProcessingRate rate) {
		super(type, name, capacity, rate);
	}

	/**
	 * Handles the event by adding the newly created job to the queue and updating
	 * its internal timer. Will result in a {@link JobProgressed} event holding the
	 * next job to process.
	 *
	 * @return {@link JobProgressed} events.
	 */
	@Override
	protected Optional<AbstractJobEvent> process(final JobInitiated jobInitiated) {
		final Job newJob = jobInitiated.getEntity();

		this.processes.add(newJob);

		if(this.processes.size()!=1) {
			return Optional.empty();
		}

		return this.scheduleNextEvent().map(j -> j);
	}

	/**
	 * Handles the job by updating its internal timer and removing the job from the
	 * queue.
	 *
	 * @return {@link JobFinished} event from that removed job, and
	 *         {@link JobProgressed} from the next job to handle.
	 */
	@Override
	public Set<AbstractJobEvent> onJobProgressed(final JobProgressed jobProgressed) {
		final Job job = jobProgressed.getEntity();

		assert MathTools.equalsDouble(0, job.getDemand()) : "Remaining demand (" + job.getDemand() + ") not zero!";

		this.processes.remove(job);

		final Optional<JobProgressed> next = this.scheduleNextEvent();
		if (next.isPresent()) {
			return Set.of(new JobFinished(job), next.get());
		}
		return Set.of(new JobFinished(job));
	}

	@Override
	public void clearJobs() {
		this.processes.clear();
	}

	/**
	 *
	 * Deprecated because not really needed for the FCFS resource.
	 *
	 * Updates the internal timer and the demand of the next job to handle by
	 * subtracting the passed time from the demand.
	 *
	 * @param simulationTime The new simulation time. Must be greater than the
	 *                       internal time.
	 *
	 *
	 */
	@Deprecated
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
	private Optional<JobProgressed> scheduleNextEvent() {
		if (this.processes.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(new JobProgressed(this.processes.peek(), this.processes.peek().getDemand()));
	}
}
