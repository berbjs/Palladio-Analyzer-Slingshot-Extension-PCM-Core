package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.ui;

import org.palladiosimulator.analyzer.slingshot.core.extension.SystemBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.ui.events.ArchitectureModelsTabBuilderStarted;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

@OnEvent(when = ArchitectureModelsTabBuilderStarted.class)
public class UsageModelLaunchConfig implements SystemBehaviorExtension {

	public static final String FILE_NAME = "usagemodel"; 
	
	@Subscribe
	public void onArchitectureModelsTab(final ArchitectureModelsTabBuilderStarted event) {
		//System.out.println("THIS WORKS!");
		event.newModelDefinition()
			 .fileName(FILE_NAME)
			 .modelClass(UsageModel.class)
			 .label("Usage Model")
			 .build();
	}
	
}
