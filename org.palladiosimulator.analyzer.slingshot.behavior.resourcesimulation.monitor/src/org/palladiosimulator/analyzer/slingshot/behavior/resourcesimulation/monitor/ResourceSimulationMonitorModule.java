package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.monitor;

import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;

public class ResourceSimulationMonitorModule extends AbstractSlingshotExtension {

	@Override
	protected void configure() {
		install(ActiveResourceMonitorInitializationBehavior.class);
		install(PassiveResourceMonitoringBehavior.class);
	}

}
