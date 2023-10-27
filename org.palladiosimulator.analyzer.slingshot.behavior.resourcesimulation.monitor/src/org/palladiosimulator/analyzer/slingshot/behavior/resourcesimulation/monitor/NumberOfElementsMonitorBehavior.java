package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.monitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.probes.NumberOfElementsInElasitcInfrastuctureProbe;
import org.palladiosimulator.analyzer.slingshot.behavior.spd.data.ModelAdjusted;
import org.palladiosimulator.analyzer.slingshot.common.annotations.Nullable;
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
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.DefaultCalculatorProbeSets;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.semanticspd.Configuration;
import org.palladiosimulator.semanticspd.ElasticInfrastructureCfg;
import org.palladiosimulator.spdmeasuringpoint.SPDResourceContainerMeasuringPoint;

/**
 *
 * Behavior to monitor the number of elements in a Elastic Infrastructure.
 *
 * The behavior creates Probes and Calculators for
 * {@link ResourceContainerMeasuringPoint}s. Beware it does *not* react to
 * {@link ResourceEnvironmentMeasuringPoint}s, as we cannot determine the target
 * group configuration from that measuring point.
 *
 * For a {@link ResourceContainerMeasuringPoint}, the behavior creates a probe
 * and a calculator for the given resource container, iff the resource container
 * is {@code unit} in any of the given target configurations.
 *
 * The metric specification must be the base metric "number of resource
 * containers".
 *
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

	private final Map<ResourceContainer, NumberOfElementsInElasitcInfrastuctureProbe> probes = new HashMap<>();

	@Inject
	public NumberOfElementsMonitorBehavior(final IGenericCalculatorFactory calculatorFactory,
			final @Nullable Configuration semanticConfiguration) {
		this.calculatorFactory = calculatorFactory;
		this.semanticConfiguration = semanticConfiguration;
	}

	@Override
	public boolean isActive() {
		return this.semanticConfiguration != null;
	}

	@Subscribe
	public Result<CalculatorRegistered> onMeasurementSpecification(final MeasurementSpecificationVisited m) {
		final MeasurementSpecification spec = m.getEntity();
		final MeasuringPoint measuringPoint = spec.getMonitor().getMeasuringPoint();

		if (measuringPoint instanceof SPDResourceContainerMeasuringPoint) {
			// Container MP --> register probe for EI where container is unit
			final SPDResourceContainerMeasuringPoint resourceContainerMeasuringPoint = (SPDResourceContainerMeasuringPoint) measuringPoint;

			if (MetricDescriptionUtility.metricDescriptionIdsEqual(spec.getMetricDescription(),
					MetricDescriptionConstants.NUMBER_OF_RESOURCE_CONTAINERS)) {

				final Optional<ElasticInfrastructureCfg> elasticInfrastructureCfg = this.semanticConfiguration
						.getTargetCfgs().stream().filter(cfg -> (cfg instanceof ElasticInfrastructureCfg))
						.map(eicfg -> ((ElasticInfrastructureCfg) eicfg)).filter(eicfg -> eicfg.getUnit().getId()
								.equals(resourceContainerMeasuringPoint.getResourceContainer().getId()))
						.findAny();

				if (elasticInfrastructureCfg.isPresent()) {
					final Calculator calculator = this.setupNumberOfElementsCalculator(resourceContainerMeasuringPoint,
							this.calculatorFactory, elasticInfrastructureCfg.get());
					return Result.of(new CalculatorRegistered(calculator));
				}
			}
		}

		return Result.empty();
	}

	@Subscribe
	public Result<ProbeTaken> onModelAdjusted(final ModelAdjusted stateUpdated) {
		final Set<ProbeTaken> probesTaken = new HashSet<>();

		for (final NumberOfElementsInElasitcInfrastuctureProbe probe : probes.values()) {
			probe.takeMeasurement(stateUpdated);
			probesTaken.add(new ProbeTaken(ProbeTakenEntity.builder().withProbe(probe).build()));
		}
		return Result.of(probesTaken);
	}

	/**
	 *
	 * @param measuringPoint
	 * @param calculatorFactory
	 * @param eiCfg
	 * @return
	 */
	public Calculator setupNumberOfElementsCalculator(final MeasuringPoint measuringPoint,
			final IGenericCalculatorFactory calculatorFactory, final ElasticInfrastructureCfg eiCfg) {
		this.probes.putIfAbsent(eiCfg.getUnit(), new NumberOfElementsInElasitcInfrastuctureProbe(eiCfg));
		final NumberOfElementsInElasitcInfrastuctureProbe probe = this.probes.get(eiCfg.getUnit());
		return calculatorFactory.buildCalculator(MetricDescriptionConstants.NUMBER_OF_RESOURCE_CONTAINERS_OVER_TIME,
				measuringPoint, DefaultCalculatorProbeSets.createSingularProbeConfiguration(probe));
	}
}
