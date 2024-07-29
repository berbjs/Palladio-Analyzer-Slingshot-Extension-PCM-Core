package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation;

import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;

public class ResourceSimulationModule extends AbstractSlingshotExtension {

	@Override
	protected void configure() {
		install(ResourceSimulation.class);
	}

	@Override
	public String getName() {
		return ResourceSimulationModule.class.getSimpleName();
	}

}
