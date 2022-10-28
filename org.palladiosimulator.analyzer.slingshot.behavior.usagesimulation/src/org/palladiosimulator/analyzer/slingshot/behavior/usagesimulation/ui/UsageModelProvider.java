package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.ui;

import javax.inject.Inject;
import javax.inject.Provider;

import org.palladiosimulator.analyzer.slingshot.core.extension.ModelProvider;
import org.palladiosimulator.analyzer.slingshot.core.extension.PCMResourceSetPartitionProvider;
import org.palladiosimulator.analyzer.slingshot.workflow.events.PCMWorkflowConfiguration;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class UsageModelProvider implements ModelProvider<UsageModel> {

	private final PCMResourceSetPartitionProvider conf;
	
	@Inject
	public UsageModelProvider(final PCMResourceSetPartitionProvider conf) {
		this.conf = conf;
	}
	
	
	@Override
	public UsageModel get() {
		return conf.get().getUsageModel();
	}
	
}
