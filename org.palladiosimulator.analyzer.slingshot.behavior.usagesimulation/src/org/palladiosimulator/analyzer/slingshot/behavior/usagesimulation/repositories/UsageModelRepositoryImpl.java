package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.repositories;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.Start;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;
import org.palladiosimulator.pcm.usagemodel.Workload;

public class UsageModelRepositoryImpl implements UsageModelRepository {

	private final Logger LOGGER = Logger.getLogger(UsageModelRepositoryImpl.class);

	private UsageModel usageModel;

	public UsageModelRepositoryImpl() {
		this.usageModel = UsagemodelFactory.eINSTANCE.createUsageModel();
	}

	@Override
	public List<UsageScenario> findAllUsageScenarios() {
		final EList<UsageScenario> usageScenarios = this.usageModel.getUsageScenario_UsageModel();
		return usageScenarios;
	}

	@Override
	public Optional<Workload> findWorkloadForUsageScenario(final UsageScenario usageScenario) {
		return this.findAllUsageScenarios().stream()
				.filter(scenario -> scenario.getId().equals(usageScenario.getId()))
				.map(UsageScenario::getWorkload_UsageScenario)
				.findFirst();
	}

	@Override
	public void load(final UsageModel usageModel) {
		this.usageModel = usageModel;
	}

	@Override
	public int findClosedWorkloadPopulation(final ClosedWorkload workload) {
		return workload.getPopulation();
	}

	@Override
	public Optional<AbstractUserAction> findFirstActionOf(final UsageScenario scenario) {
		return scenario.getScenarioBehaviour_UsageScenario().getActions_ScenarioBehaviour().stream()
				.filter(Start.class::isInstance)
				.findFirst();
	}

}
