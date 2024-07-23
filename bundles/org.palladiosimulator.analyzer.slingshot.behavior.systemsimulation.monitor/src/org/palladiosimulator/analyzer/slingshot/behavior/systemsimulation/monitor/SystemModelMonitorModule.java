package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.monitor;

import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;

public class SystemModelMonitorModule extends AbstractSlingshotExtension {

	@Override
	protected void configure() {
		install(OperationCallActionResponseTimeMonitoringBehavior.class);
		install(NumberOfElementsMonitorBehavior.class);
	}
}
