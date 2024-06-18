package org.palladiosimulator.analyzer.slingshot.behavior.usageevolution;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.core.extension.ModelProvider;
import org.palladiosimulator.analyzer.slingshot.core.extension.PCMResourceSetPartitionProvider;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.scaledl.usageevolution.UsageEvolution;
import org.scaledl.usageevolution.UsageevolutionPackage;

public class UsageEvolutionModelProvider implements ModelProvider<UsageEvolution> {

	private static final Logger LOGGER = Logger.getLogger(UsageEvolutionModelProvider.class);


	private final PCMResourceSetPartitionProvider partition;

	@Inject
	public UsageEvolutionModelProvider(final PCMResourceSetPartitionProvider partition) {
		this.partition = partition;
	}

	@Override
	public UsageEvolution get() {
		final List<EObject> evolutions = partition.get()
				.getElement(UsageevolutionPackage.eINSTANCE.getUsageEvolution());
		if (evolutions.size() == 0) {
			LOGGER.warn("UsageEvolution not present: List size is 0.");
			return null;
		}
		return (UsageEvolution) evolutions.get(0);
	}

}
