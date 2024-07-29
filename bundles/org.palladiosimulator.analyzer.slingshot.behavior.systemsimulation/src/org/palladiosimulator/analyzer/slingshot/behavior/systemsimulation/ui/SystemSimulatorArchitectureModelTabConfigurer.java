package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.ui;

import java.util.List;

import org.palladiosimulator.analyzer.slingshot.core.extension.SystemBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.ui.events.ArchitectureModelsTabBuilderStarted;
import org.palladiosimulator.analyzer.slingshot.workflow.events.WorkflowLaunchConfigurationBuilderInitialized;
import org.palladiosimulator.pcm.allocation.Allocation;

@OnEvent(when = ArchitectureModelsTabBuilderStarted.class)
@OnEvent(when = WorkflowLaunchConfigurationBuilderInitialized.class)
public class SystemSimulatorArchitectureModelTabConfigurer implements SystemBehaviorExtension {

	public static final String FILE_NAME = "allocationModel"; 
	public static final String[] FILE_EXTENSIONS = new String[] { "*.allocation" };
	
	@Subscribe
	public void onArchitectureModelsTab(final ArchitectureModelsTabBuilderStarted event) {
		event.newModelDefinition()
			 .fileName(FILE_NAME)
			 .fileExtensions(FILE_EXTENSIONS)
			 .modelClass(Allocation.class)
			 .label("Allocation Model")
			 .build();
	}
	
	@Subscribe
	public void onWorkflowConfigurationInitialized(final WorkflowLaunchConfigurationBuilderInitialized event) {
		event.getConfiguration(FILE_NAME, 
				"", 
				(conf, modelFile) -> conf.setAllocationFiles(List.of((String) modelFile)));
	}
}
