package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.monitor;

import java.util.Optional;
import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ActiveResourceStateUpdated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ResourceDemandCalculated;
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
import org.palladiosimulator.pcmmeasuringpoint.ActiveResourceMeasuringPoint;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.probeframework.probes.Probe;

/**
 *
 * Behavior to monitor active resources.
 *
 * TODO : would it make sense, to unregister / only register subscribers, if the
 * respective monitor actually exists? i.e. only listen to
 * {@link ActiveResourceStateUpdated} if at least one monitor on an active
 * resource exists.
 *
 * @author Sarah Stieß
 *
 */
@OnEvent(when = MonitorModelVisited.class, then = CalculatorRegistered.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = ActiveResourceStateUpdated.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = ResourceDemandCalculated.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
public class ActiveResourceMonitorBehavior implements SimulationBehaviorExtension {

	private final IGenericCalculatorFactory calculatorFactory;
	private final ActiveResourceProbeTable table = new ActiveResourceProbeTable();

	@Inject
	public ActiveResourceMonitorBehavior(final IGenericCalculatorFactory calculatorFactory) {
		this.calculatorFactory = calculatorFactory;
	}

	@Subscribe
	public Result<CalculatorRegistered> onMeasurementSpecification(final MeasurementSpecificationVisited m) {
		final MeasurementSpecification spec = m.getEntity();
		final MeasuringPoint measuringPoint = spec.getMonitor().getMeasuringPoint();

		if (measuringPoint instanceof ActiveResourceMeasuringPoint) {

			final ActiveResourceMeasuringPoint activeResourceMeasuringPoint = (ActiveResourceMeasuringPoint) measuringPoint;

			if (MetricDescriptionUtility.metricDescriptionIdsEqual(spec.getMetricDescription(),
					MetricDescriptionConstants.STATE_OF_ACTIVE_RESOURCE_METRIC)) {
				final Calculator calculator = this.table.setupStateOfActiveResourceCalculator(activeResourceMeasuringPoint,
						this.calculatorFactory);
				return Result.of(new CalculatorRegistered(calculator));

			} else if (MetricDescriptionUtility.metricDescriptionIdsEqual(spec.getMetricDescription(),
					MetricDescriptionConstants.RESOURCE_DEMAND_METRIC)) {
				final Calculator calculator = this.table.setupResoucreDemandCalculator(activeResourceMeasuringPoint,
						this.calculatorFactory);
				return Result.of(new CalculatorRegistered(calculator));

			} else if (MetricDescriptionUtility.metricDescriptionIdsEqual(spec.getMetricDescription(),
					MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE)) {

				throw new UnsupportedOperationException("UTILIZATION_OF_ACTIVE_RESOURCE not yet implemented");
			}
		}
		return Result.empty();
	}

	@Subscribe
	public Result<ProbeTaken> onResourceDemandCalculated(final ResourceDemandCalculated stateUpdated) {
		final Optional<Probe> probe = this.table.currentResourceDemand(stateUpdated);
		if (probe.isPresent()) {
			return Result.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(probe.get()).build()));
		}
		return Result.of();
	}

	@Subscribe
	public Result<ProbeTaken> onActiveResourceStateUpdated(final ActiveResourceStateUpdated stateUpdated) {
		final Optional<Probe> probe = this.table.currentStateOfActiveResource(stateUpdated);
		if (probe.isPresent()) {
			return Result.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(probe.get()).build()));
		}
		return Result.of();
	}

}
