package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources;

import java.util.HashMap;
import java.util.Map;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.IResource;

public abstract class AbstractResourceTable<K, R extends IResource> {

	protected final Map<K, R> resources;

	public AbstractResourceTable() {
		this.resources = new HashMap<>();
	}

	public void clearResourcesFromJobs() {
		this.resources.values().forEach(IResource::clearJobs);
	}
}
