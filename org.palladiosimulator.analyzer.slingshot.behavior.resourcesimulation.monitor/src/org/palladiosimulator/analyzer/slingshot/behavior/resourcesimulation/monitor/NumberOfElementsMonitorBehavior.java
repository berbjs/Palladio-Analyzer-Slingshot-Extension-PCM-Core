package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.monitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.probes.NumberOfElementsInResourceEnvironmentProbe;
import org.palladiosimulator.analyzer.slingshot.behavior.spd.data.ModelAdjusted;
import org.palladiosimulator.analyzer.slingshot.behavior.spd.data.adjustment.ModelChange;
import org.palladiosimulator.analyzer.slingshot.behavior.spd.data.adjustment.ResourceEnvironmentChange;
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
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcmmeasuringpoint.ResourceEnvironmentMeasuringPoint;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.DefaultCalculatorProbeSets;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.probeframework.probes.Probe;

/**
 *
 * Behavior to monitor the number of elements in a Elastic Infrastructure.
 *
 *
 * @author Sarah Stieß
 *
 */
@OnEvent(when = MonitorModelVisited.class, then = CalculatorRegistered.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = ModelAdjusted.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
public class NumberOfElementsMonitorBehavior implements SimulationBehaviorExtension {

	private final IGenericCalculatorFactory calculatorFactory;

	// we only need this map, when we measure per EI cfg unit (i.e. container based)
	// or if there are multiple Resource environments (not intended by palladio)
	private final Map<ResourceEnvironment, Probes> probes = new HashMap<>();

	@Inject
	public NumberOfElementsMonitorBehavior(final IGenericCalculatorFactory calculatorFactory) {
		this.calculatorFactory = calculatorFactory;
	}

	@Subscribe
	public Result<CalculatorRegistered> onMeasurementSpecification(final MeasurementSpecificationVisited m) {
		final MeasurementSpecification spec = m.getEntity();
		final MeasuringPoint measuringPoint = spec.getMonitor().getMeasuringPoint();

		if (measuringPoint instanceof ResourceEnvironmentMeasuringPoint) {

			final ResourceEnvironmentMeasuringPoint resourceEnvironmentMeasuringPoint = (ResourceEnvironmentMeasuringPoint) measuringPoint;

			if (MetricDescriptionUtility.metricDescriptionIdsEqual(spec.getMetricDescription(),
					MetricDescriptionConstants.NUMBER_OF_RESOURCE_CONTAINERS)) {
				final Calculator calculator = this.setupNumberOfElementsCalculator(resourceEnvironmentMeasuringPoint,
						this.calculatorFactory);
				return Result.of(new CalculatorRegistered(calculator));

			}
		}
		return Result.empty();
	}

	@Subscribe
	public Result<ProbeTaken> onModelAdjusted(final ModelAdjusted stateUpdated) {
		final Optional<Probe> probe = this.currentNumberOfElements(stateUpdated);
		if (probe.isPresent()) {
			return Result.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(probe.get()).build()));
		}
		return Result.of();
	}

	/**
	 * TODO : do we considers multiple Resource environments?
	 *
	 * @param event
	 * @return
	 */
	public Optional<Probe> currentNumberOfElements(final ModelAdjusted event) {
		for (final ModelChange<?> change : event.getChanges()) {
			if (change instanceof ResourceEnvironmentChange) {

				final ResourceEnvironmentChange resChange = (ResourceEnvironmentChange) change;

				final Probes probes = this.probes
						.get(resChange.getOldResourceContainers().get(0).getResourceEnvironment_ResourceContainer());

				if (probes != null) {
					// TODO : get number of containers from config, capsulate into event, to put it
					// into the Probe o_O
					// 1. create the respective "fake" event o_O
					// or : over write tak -- nah, the i gotta over write every thing o_O
					// can i go by with "just" the elements from the model adjusted?
					probes.numberOfElementsAtElasticInfrastructure.takeMeasurement(event);
					return Optional.of(probes.numberOfElementsAtElasticInfrastructure);
				}
			}
		}

		return Optional.empty();
	}

	public Calculator setupNumberOfElementsCalculator(final ResourceEnvironmentMeasuringPoint measuringPoint,
			final IGenericCalculatorFactory calculatorFactory) {
		this.probes.putIfAbsent(measuringPoint.getResourceEnvironment(), new Probes());
		final Probes probes = this.probes.get(measuringPoint.getResourceEnvironment());
		return calculatorFactory.buildCalculator(MetricDescriptionConstants.NUMBER_OF_RESOURCE_CONTAINERS_OVER_TIME,
				measuringPoint, DefaultCalculatorProbeSets
						.createSingularProbeConfiguration(probes.numberOfElementsAtElasticInfrastructure));
	}

	/**
	 *
	 *
	 *
	 * @author Sarah Stieß
	 *
	 */
	private static final class Probes {
		// create probe.
		private final NumberOfElementsInResourceEnvironmentProbe numberOfElementsAtElasticInfrastructure = new NumberOfElementsInResourceEnvironmentProbe();
	}
}
