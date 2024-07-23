package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import java.util.Optional;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;

/**
 * Tells that the Job has been aborted. Unlike {@link JobFinished}, this signals
 * that there might have been an error and should be retried.
 * 
 * @author Julijan Katic
 */
public class JobAborted extends AbstractJobEvent {

	private final Optional<Throwable> exception;
	private final String reason;

	public JobAborted(final Job entity, final double delay, final Throwable exception, final String reason) {
		super(entity, delay);
		this.exception = Optional.ofNullable(exception);
		this.reason = reason;
	}

	public JobAborted(final Job entity, final double delay, final String reason) {
		this(entity, delay, null, reason);
	}

	public JobAborted(final Job entity, final double delay, final Throwable exception) {
		this(entity, delay, exception, null);
	}

	public JobAborted(final Job entity, final double delay) {
		this(entity, delay, null, null);
	}

	public Optional<Throwable> getException() {
		return this.exception;
	}

	public String getReason() {
		if (reason == null) {
			return this.exception.map(Throwable::getMessage).orElse("");
		}

		return reason;
	}

}
