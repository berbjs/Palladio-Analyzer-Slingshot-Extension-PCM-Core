package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.monitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.monitor.probes.NumberOfElementsInCompetingConsumerGroupProbe;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.monitor.probes.NumberOfElementsInServiceGroupProbe;
import org.palladiosimulator.analyzer.slingshot.common.annotations.Nullable;
import org.palladiosimulator.analyzer.slingshot.common.events.modelchanges.ModelAdjusted;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.analyzer.slingshot.monitor.data.entities.ProbeTakenEntity;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.CalculatorRegistered;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.ProbeTaken;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.modelvisited.MeasurementSpecificationVisited;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.modelvisited.MonitorModelVisited;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.DefaultCalculatorProbeSets;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.semanticspd.CompetingConsumersGroupCfg;
import org.palladiosimulator.semanticspd.Configuration;
import org.palladiosimulator.semanticspd.ServiceGroupCfg;
import org.palladiosimulator.spdmeasuringpoint.CompetingConsumerGroupMeasuringPoint;
import org.palladiosimulator.spdmeasuringpoint.ServiceGroupMeasuringPoint;

/**
 *
 * Behavior to monitor the number of elements in a Service Group or a Competing
 * Consumer Group.
 *
 * The behavior creates Probes and Calculators for
 * {@link CompetingConsumerGroupMeasuringPoint}s and
 * {@link ServiceGroupMeasuringPoint}s, if semantic SPD configurations for the
 * target groups exist.
 *
 * TODO fixme : Currently, the metric specification must be the base metric
 * "number of resource containers" which is semantically completely wrong but
 * still works, because its plain numbers.
 *
 *
 * @author Sarah Stieß
 *
 */
@OnEvent(when = MonitorModelVisited.class, then = CalculatorRegistered.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = ModelAdjusted.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
public class NumberOfElementsMonitorBehavior implements SimulationBehaviorExtension {

	private final IGenericCalculatorFactory calculatorFactory;
	private final Configuration semanticConfiguration;

	private final Map<AssemblyContext, NumberOfElementsInServiceGroupProbe> serviceGroupProbes = new HashMap<>();
	private final Map<AssemblyContext, NumberOfElementsInCompetingConsumerGroupProbe> competingConsumerProbes = new HashMap<>();

	@Inject
	public NumberOfElementsMonitorBehavior(final IGenericCalculatorFactory calculatorFactory,
			final @Nullable Configuration semanticConfiguration) {
		this.calculatorFactory = calculatorFactory;
		this.semanticConfiguration = semanticConfiguration;
	}

	@Override
	public boolean isActive() {
		return this.semanticConfiguration != null && !this.semanticConfiguration.getTargetCfgs().isEmpty();
	}

	@Subscribe
	public Result<CalculatorRegistered> onMeasurementSpecification(final MeasurementSpecificationVisited m) {
		final MeasurementSpecification spec = m.getEntity();
		final MeasuringPoint measuringPoint = spec.getMonitor().getMeasuringPoint();

		if (measuringPoint instanceof final ServiceGroupMeasuringPoint mp
				&& MetricDescriptionUtility.metricDescriptionIdsEqual(spec.getMetricDescription(),
						MetricDescriptionConstants.NUMBER_OF_RESOURCE_CONTAINERS)) { // TODO Fix Metric description

			final Optional<Calculator> sgCalculator = regsterSGCalculator(mp);

			if (sgCalculator.isPresent()) {
				return Result.of(new CalculatorRegistered(sgCalculator.get()));

			}
		}

		if (measuringPoint instanceof final CompetingConsumerGroupMeasuringPoint mp
				&& MetricDescriptionUtility.metricDescriptionIdsEqual(spec.getMetricDescription(),
						MetricDescriptionConstants.NUMBER_OF_RESOURCE_CONTAINERS)) { // TODO Fix Metric description

			final Optional<Calculator> ccgCalculator = registerCCGCalculator(mp);

			if (ccgCalculator.isPresent()) {
				return Result.of(new CalculatorRegistered(ccgCalculator.get()));

			}
		}

		return Result.empty();
	}

	/**
	 * Register a Calculator if the AssemblyContext of the given MP is a
	 * {@code unit} in a {@link ServiceGroupCfg}.
	 *
	 * @param assemblyContextMeasuringPoint MP to register a calculator for
	 * @return the calculator, iff it got registered
	 */
	private Optional<Calculator> registerCCGCalculator(
			final CompetingConsumerGroupMeasuringPoint assemblyContextMeasuringPoint) {
		final Optional<CompetingConsumersGroupCfg> competingConsumerGroupCfg = this.semanticConfiguration
				.getTargetCfgs().stream().filter(cfg -> (cfg instanceof CompetingConsumersGroupCfg))
				.map(eicfg -> ((CompetingConsumersGroupCfg) eicfg))
				.filter(eicfg -> eicfg.getUnit().getId()
						.equals(assemblyContextMeasuringPoint.getCompetingConsumerGroup().getUnitAssembly().getId()))
				.findAny();

		if (competingConsumerGroupCfg.isPresent()) {
			final Calculator calculator = this.setupNumberOfElementsCalculator(assemblyContextMeasuringPoint,
					this.calculatorFactory, competingConsumerGroupCfg.get());
			return Optional.of(calculator);
		}
		return Optional.empty();
	}

	/**
	 * Register a Calculator if the AssemblyContext of the given MP is a
	 * {@code unit} in a {@link CompetingConsumersGroupCfg}.
	 *
	 * @param assemblyContextMeasuringPoint MP to register a calculator for
	 * @return the calculator, iff it got registered
	 */
	private Optional<Calculator> regsterSGCalculator(final ServiceGroupMeasuringPoint assemblyContextMeasuringPoint) {
		final Optional<ServiceGroupCfg> serviceGroupCfg = this.semanticConfiguration.getTargetCfgs().stream()
				.filter(cfg -> (cfg instanceof ServiceGroupCfg)).map(eicfg -> ((ServiceGroupCfg) eicfg))
				.filter(eicfg -> eicfg.getUnit().getId()
						.equals(assemblyContextMeasuringPoint.getServiceGroup().getUnitAssembly().getId()))
				.findAny();

		if (serviceGroupCfg.isPresent()) {
			final Calculator calculator = this.setupNumberOfElementsCalculator(assemblyContextMeasuringPoint,
					this.calculatorFactory, serviceGroupCfg.get());
			return Optional.of(calculator);
		}
		return Optional.empty();
	}

	@Subscribe
	public Result<ProbeTaken> onModelAdjusted(final ModelAdjusted stateUpdated) {
		final Set<ProbeTaken> probesTaken = new HashSet<>();

		for (final NumberOfElementsInServiceGroupProbe probe : serviceGroupProbes.values()) {
			probe.takeMeasurement(stateUpdated);
			probesTaken.add(new ProbeTaken(ProbeTakenEntity.builder().withProbe(probe).build()));
		}

		for (final NumberOfElementsInCompetingConsumerGroupProbe probe : competingConsumerProbes.values()) {
			probe.takeMeasurement(stateUpdated);
			probesTaken.add(new ProbeTaken(ProbeTakenEntity.builder().withProbe(probe).build()));
		}
		return Result.of(probesTaken);
	}

	/**
	 *
	 * @param measuringPoint
	 * @param calculatorFactory
	 * @param sgCfg
	 * @return the new calculator
	 */
	public Calculator setupNumberOfElementsCalculator(final MeasuringPoint measuringPoint,
			final IGenericCalculatorFactory calculatorFactory, final ServiceGroupCfg sgCfg) {
		this.serviceGroupProbes.putIfAbsent(sgCfg.getUnit(), new NumberOfElementsInServiceGroupProbe(sgCfg));
		final NumberOfElementsInServiceGroupProbe probe = this.serviceGroupProbes.get(sgCfg.getUnit());
		return calculatorFactory.buildCalculator(MetricDescriptionConstants.NUMBER_OF_RESOURCE_CONTAINERS_OVER_TIME,
				measuringPoint, DefaultCalculatorProbeSets.createSingularProbeConfiguration(probe));
	}

	/**
	 *
	 * @param measuringPoint
	 * @param calculatorFactory
	 * @param sgCfg
	 * @return the new calculator
	 */
	public Calculator setupNumberOfElementsCalculator(final MeasuringPoint measuringPoint,
			final IGenericCalculatorFactory calculatorFactory, final CompetingConsumersGroupCfg sgCfg) {
		this.competingConsumerProbes.putIfAbsent(sgCfg.getUnit(),
				new NumberOfElementsInCompetingConsumerGroupProbe(sgCfg));
		final NumberOfElementsInCompetingConsumerGroupProbe probe = this.competingConsumerProbes.get(sgCfg.getUnit());
		return calculatorFactory.buildCalculator(MetricDescriptionConstants.NUMBER_OF_RESOURCE_CONTAINERS_OVER_TIME,
				measuringPoint, DefaultCalculatorProbeSets.createSingularProbeConfiguration(probe));
	}
}
