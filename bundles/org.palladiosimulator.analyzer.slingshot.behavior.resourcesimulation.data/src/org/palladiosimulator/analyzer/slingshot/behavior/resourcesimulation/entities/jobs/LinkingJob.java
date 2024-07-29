package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.CallOverWireRequest;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;

public class LinkingJob extends Job {

	private final LinkingResource linkingResource;
	private final CallOverWireRequest request;

	public LinkingJob(final String id, final double demand, final LinkingResource linkingResource,
			final CallOverWireRequest request) {
		super(id, demand);
		this.linkingResource = linkingResource;
		this.request = request;
	}

	public LinkingResource getLinkingResource() {
		return linkingResource;
	}

	public CallOverWireRequest getRequest() {
		return request;
	}
}
