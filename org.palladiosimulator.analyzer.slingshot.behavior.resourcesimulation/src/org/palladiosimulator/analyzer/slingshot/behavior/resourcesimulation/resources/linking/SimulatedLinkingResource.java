package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.linking;

import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.LinkingJob;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.ProcessingRate;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.AbstractJobEvent;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobAborted;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active.FCFSResource;
import org.palladiosimulator.pcm.resourceenvironment.CommunicationLinkResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;

public class SimulatedLinkingResource extends FCFSResource {

	private final ProcessingRate latency;
	private final double failureRate;
	private final LinkingResource linkingResource;

	public SimulatedLinkingResource(final LinkingResource linkingResource) {
		super(null, linkingResource.getEntityName(), 1, new ProcessingRate(linkingResource
				.getCommunicationLinkResourceSpecifications_LinkingResource()
				.getThroughput_CommunicationLinkResourceSpecification()));
		
		final CommunicationLinkResourceSpecification spec = linkingResource
				.getCommunicationLinkResourceSpecifications_LinkingResource();

		this.linkingResource = linkingResource;
		this.latency = new ProcessingRate(spec.getLatency_CommunicationLinkResourceSpecification());
		this.failureRate = spec.getFailureProbability();
	}

	@Override
	public Set<AbstractJobEvent> onJobProgressed(final JobProgressed jobProgressed) {
		final double ranNumber = Math.random();
		final Job job = jobProgressed.getEntity();

		if (ranNumber < this.failureRate) {
			this.abortJob(job);

			return Set.of(new JobAborted(job, 0, String.format(
					"Linking resource simulated a failure: Number was %f and thus within the failure rate of %f",
					ranNumber, this.failureRate)));
		}

		job.updateDemand(job.getDemand() + latency.calculateRV());
		return super.onJobProgressed(jobProgressed);
	}

	@Override
	public boolean jobBelongsToResource(final Job job) {
		if (job instanceof LinkingJob) {
			final LinkingJob linkingJob = (LinkingJob) job;
			return linkingJob.getLinkingResource().getId().equals(linkingResource.getId());
		}
		return false;
	}

	public LinkingResource getLinkingResource() {
		return linkingResource;
	}

}
