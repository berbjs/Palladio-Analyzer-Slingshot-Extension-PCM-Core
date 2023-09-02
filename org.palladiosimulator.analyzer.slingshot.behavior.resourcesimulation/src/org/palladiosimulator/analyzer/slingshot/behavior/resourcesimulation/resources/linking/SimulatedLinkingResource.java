package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.linking;

import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.ProcessingRate;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.AbstractJobEvent;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobAborted;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.AbstractResource;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active.ActiveResource;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active.FCFSResource;
import org.palladiosimulator.pcm.resourceenvironment.CommunicationLinkResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;

public class SimulatedLinkingResource extends AbstractResource implements ActiveResource {

	private final ProcessingRate latency;
	private final ProcessingRate throughput;
	private final double failureRate;
	private final FCFSResource underlyingResource;

	public SimulatedLinkingResource(final String name, final LinkingResource linkingResource) {
		super(1, name, linkingResource.getId());

		final CommunicationLinkResourceSpecification spec = linkingResource
				.getCommunicationLinkResourceSpecifications_LinkingResource();

		this.latency = new ProcessingRate(spec.getLatency_CommunicationLinkResourceSpecification());
		this.failureRate = spec.getFailureProbability();
		this.throughput = new ProcessingRate(spec.getThroughput_CommunicationLinkResourceSpecification());
		this.underlyingResource = new FCFSResource(null, name, 1, throughput);
	}

	@Override
	public void clearJobs() {
		underlyingResource.clearJobs();
	}

	@Override
	public Set<AbstractJobEvent> onJobInitiated(final JobInitiated jobInitiated) {
		return underlyingResource.onJobInitiated(jobInitiated);
	}

	@Override
	public Set<AbstractJobEvent> onJobProgressed(final JobProgressed jobProgressed) {
		final double ranNumber = Math.random();
		final Job job = jobProgressed.getEntity();

		if (ranNumber < this.failureRate) {
			underlyingResource.abortJob(job);

			return Set.of(new JobAborted(job, 0, String.format(
					"Linking resource simulated a failure: Number was %f and thus within the failure rate of %f",
					ranNumber, this.failureRate)));
		}

		job.updateDemand(job.getDemand() + latency.calculateRV());
		return underlyingResource.onJobProgressed(jobProgressed);
	}

}
