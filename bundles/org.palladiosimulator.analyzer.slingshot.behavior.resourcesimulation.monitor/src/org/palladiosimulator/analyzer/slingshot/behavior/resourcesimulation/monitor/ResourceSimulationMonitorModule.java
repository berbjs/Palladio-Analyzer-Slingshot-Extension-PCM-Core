package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.monitor;

import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;

public class ResourceSimulationMonitorModule extends AbstractSlingshotExtension {

	@Override
	protected void configure() {
		install(ActiveResourceMonitorBehavior.class);
		install(PassiveResourceMonitoringBehavior.class);
		install(NumberOfElementsMonitorBehavior.class);
	}

}
