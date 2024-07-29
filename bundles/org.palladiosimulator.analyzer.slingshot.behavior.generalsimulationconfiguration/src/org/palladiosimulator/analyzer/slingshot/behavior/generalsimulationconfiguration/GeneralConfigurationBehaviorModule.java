package org.palladiosimulator.analyzer.slingshot.behavior.generalsimulationconfiguration;

import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;

public class GeneralConfigurationBehaviorModule extends AbstractSlingshotExtension {

	@Override
	protected void configure() {
		install(GeneralSimulationConfigurationBehavior.class);
	}

	@Override
	public String getName() {
		return GeneralConfigurationBehaviorModule.class.getSimpleName();
	}

}
