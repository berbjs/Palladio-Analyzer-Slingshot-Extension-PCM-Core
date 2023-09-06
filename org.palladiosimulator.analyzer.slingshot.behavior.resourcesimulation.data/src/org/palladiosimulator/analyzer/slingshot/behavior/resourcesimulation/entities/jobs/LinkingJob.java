package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs;

import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;

public class LinkingJob extends Job {

	private final LinkingResource linkingResource;

	public LinkingJob(final String id, final double demand, final LinkingResource linkingResource) {
		super(id, demand);
		this.linkingResource = linkingResource;
	}

	public LinkingResource getLinkingResource() {
		return linkingResource;
	}

}
