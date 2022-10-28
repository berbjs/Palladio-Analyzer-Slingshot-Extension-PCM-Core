package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation;

import javax.inject.Inject;
import javax.inject.Provider;

import org.palladiosimulator.analyzer.slingshot.core.extension.ModelProvider;
import org.palladiosimulator.analyzer.slingshot.core.extension.PCMResourceSetPartitionProvider;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.pcm.allocation.Allocation;

public class AllocationModelProvider implements ModelProvider<Allocation> {

	private final PCMResourceSetPartitionProvider partition;
	
	@Inject
	public AllocationModelProvider(final PCMResourceSetPartitionProvider partition) {
		this.partition = partition;
	}
	
	@Override
	public Allocation get() {
		return partition.get().getAllocation();
	}
	
}
