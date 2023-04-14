package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.monitor;

import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ActiveResourceStateUpdated;
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
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcmmeasuringpoint.ActiveResourceMeasuringPoint;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.probeframework.probes.Probe;


@OnEvent(when = MonitorModelVisited.class, then = CalculatorRegistered.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = ActiveResourceStateUpdated.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
public class ActiveResourceMonitorInitializationBehavior implements SimulationBehaviorExtension {
	// RESOURCE_DEMAND_METRIC_TUPLE
	// STATE_OF_ACTIVE_RESOURCE_METRIC_TUPLE
	// UTILIZATION_OF_ACTIVE_RESOURCE_TUPLE


	private final IGenericCalculatorFactory calculatorFactory;

	private final ActiveResourceProbeTable table = new ActiveResourceProbeTable();

	@Inject
	public ActiveResourceMonitorInitializationBehavior(final IGenericCalculatorFactory calculatorFactory) {
		this.calculatorFactory = calculatorFactory;
	}

	@Subscribe
	public Result<CalculatorRegistered> onMeasurementSpecification(final MeasurementSpecificationVisited m) {
		final MeasurementSpecification spec = m.getEntity();
		final MeasuringPoint measuringPoint = spec.getMonitor().getMeasuringPoint();

		if (measuringPoint instanceof ActiveResourceMeasuringPoint) {

			final ActiveResourceMeasuringPoint activeResourceMeasuringPoint = (ActiveResourceMeasuringPoint) measuringPoint;
			final ProcessingResourceSpecification activeResourceSpec = activeResourceMeasuringPoint.getActiveResource();
			this.table.addActiveResource(activeResourceSpec.getResourceContainer_ProcessingResourceSpecification());

			if (MetricDescriptionUtility.metricDescriptionIdsEqual(spec.getMetricDescription(),
					MetricDescriptionConstants.STATE_OF_ACTIVE_RESOURCE_METRIC)) {
				final Calculator calculator = this.table.setupStateOfActiveResourceCalculator(activeResourceMeasuringPoint,
						this.calculatorFactory);
				return Result.of(new CalculatorRegistered(calculator));

			} else if (MetricDescriptionUtility.metricDescriptionIdsEqual(spec.getMetricDescription(),
					MetricDescriptionConstants.RESOURCE_DEMAND_METRIC)) {
				throw new IllegalArgumentException("Not Yet Supported 1.");

			} else if (MetricDescriptionUtility.metricDescriptionIdsEqual(spec.getMetricDescription(),
					MetricDescriptionConstants.UTILIZATION_OF_ACTIVE_RESOURCE)) {
				throw new IllegalArgumentException("Not Yet Supported 2.");
			}
		}
		return Result.empty();

	}

	@Subscribe
	public Result<ProbeTaken> onActiveResourceStateUpdated(final ActiveResourceStateUpdated stateUpdated) {
			final Probe probe = this.table.currentStateOfActiveResource(stateUpdated);
			return Result.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(probe).build()));
	}

}
