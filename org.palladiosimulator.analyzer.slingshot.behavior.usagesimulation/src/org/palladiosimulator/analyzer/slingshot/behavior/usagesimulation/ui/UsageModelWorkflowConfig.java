package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.ui;

import org.palladiosimulator.analyzer.slingshot.core.extension.SystemBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.workflow.events.WorkflowLaunchConfigurationBuilderInitialized;

@OnEvent(when = WorkflowLaunchConfigurationBuilderInitialized.class)
public class UsageModelWorkflowConfig implements SystemBehaviorExtension {

	@Subscribe
	public void onWorkflowConfiguration(final WorkflowLaunchConfigurationBuilderInitialized init) {
		init.getConfiguration(UsageModelLaunchConfig.FILE_NAME, 
				"usageModel", 
				(conf, model) -> conf.setUsageModelFile((String) model));
	}
	
}
