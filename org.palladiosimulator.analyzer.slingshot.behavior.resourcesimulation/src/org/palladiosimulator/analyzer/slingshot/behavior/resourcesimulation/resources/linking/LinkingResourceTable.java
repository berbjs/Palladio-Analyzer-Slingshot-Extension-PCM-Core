package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.linking;

import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.AbstractResourceTable;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

/**
 * This table holds all the references to the linking resources, as well as the
 * ability to find the linking resource between two resource containers.
 * 
 * @author Julijan Katic
 */
public class LinkingResourceTable extends AbstractResourceTable<String, SimulatedLinkingResource> {

	public void buildTable(final Allocation allocation) {
		allocation.getTargetResourceEnvironment_Allocation().getLinkingResources__ResourceEnvironment()
				.forEach(this::createSimulatedLinkingResource);
	}

	public void createSimulatedLinkingResource(final LinkingResource linkingResource) {
		this.resources.put(linkingResource.getId(), new SimulatedLinkingResource(linkingResource));
	}

	public List<SimulatedLinkingResource> findLinkingResourceBetweenContainers(final ResourceContainer from, final ResourceContainer to) {
		return this.resources.values().stream()
				.filter(lk -> isConnectedBy(from, to, lk.getLinkingResource()))
				.collect(Collectors.toList());
	}

	private boolean isConnectedBy(final ResourceContainer from, final ResourceContainer to, final LinkingResource linkingResource) {
		return containerInLinkingResource(from, linkingResource) && containerInLinkingResource(to, linkingResource);
	}

	private boolean containerInLinkingResource(final ResourceContainer rc, final LinkingResource linkingResource) {
		return linkingResource.getConnectedResourceContainers_LinkingResource().stream()
				.anyMatch(r -> r.getId().equals(rc.getId()));
	}

}
