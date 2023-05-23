package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.monitor;

import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;

public class SystenModelMonitorModule extends AbstractSlingshotExtension {

	@Override
	protected void configure() {
		install(OperationCallActionResponseTimeMonitoringBehavior.class);
	}

}
