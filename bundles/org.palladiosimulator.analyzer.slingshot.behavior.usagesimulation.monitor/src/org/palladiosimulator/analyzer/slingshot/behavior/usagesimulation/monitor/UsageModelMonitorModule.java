package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.monitor;

import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;

public class UsageModelMonitorModule extends AbstractSlingshotExtension {

	@Override
	protected void configure() {
		install(UsageScenarioResponseTimeMonitoringBehavior.class);
	}

}
