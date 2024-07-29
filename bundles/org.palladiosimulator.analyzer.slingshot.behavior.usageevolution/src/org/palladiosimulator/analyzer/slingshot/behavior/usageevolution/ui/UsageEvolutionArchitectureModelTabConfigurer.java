package org.palladiosimulator.analyzer.slingshot.behavior.usageevolution.ui;

import org.palladiosimulator.analyzer.slingshot.core.extension.SystemBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.ui.events.ArchitectureModelsTabBuilderStarted;
import org.palladiosimulator.analyzer.slingshot.workflow.events.WorkflowLaunchConfigurationBuilderInitialized;
import org.palladiosimulator.pcm.allocation.Allocation;

@OnEvent(when = ArchitectureModelsTabBuilderStarted.class)
@OnEvent(when = WorkflowLaunchConfigurationBuilderInitialized.class)
public class UsageEvolutionArchitectureModelTabConfigurer implements SystemBehaviorExtension {

	public static final String FILE_NAME = "usageEvolution";
	public static final String[] FILE_EXTENSIONS = new String[] { "*.usageevolution" };

	@Subscribe
	public void onArchitectureModelsTab(final ArchitectureModelsTabBuilderStarted event) {
		event.newModelDefinition()
			 .fileName(FILE_NAME)
				.optional(true)
			 .fileExtensions(FILE_EXTENSIONS)
			 .modelClass(Allocation.class)
				.label("UsageEvolution Model")
			 .build();
	}

	@Subscribe
	public void onWorkflowConfigurationInitialized(final WorkflowLaunchConfigurationBuilderInitialized event) {
		event.getConfiguration(FILE_NAME,
				"",
				(conf, modelFile) -> conf.addOtherModelFile((String) modelFile));
	}
}
