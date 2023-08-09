package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.monitor;

import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.PassiveResourceStateUpdated;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest.ResourceType;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.PassiveResourceAcquired;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.PassiveResourceReleased;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.ResourceDemandRequested;
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
import org.palladiosimulator.pcmmeasuringpoint.AssemblyPassiveResourceMeasuringPoint;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.probeframework.probes.Probe;

@OnEvent(when = MonitorModelVisited.class, then = CalculatorRegistered.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = ResourceDemandRequested.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = PassiveResourceAcquired.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = PassiveResourceReleased.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = PassiveResourceStateUpdated.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
public class PassiveResourceMonitoringBehavior implements SimulationBehaviorExtension {

	private final IGenericCalculatorFactory calculatorFactory;

	private final PassiveResourceProbeTable table = new PassiveResourceProbeTable();

	@Inject
	public PassiveResourceMonitoringBehavior(final IGenericCalculatorFactory calculatorFactory) {
		this.calculatorFactory = calculatorFactory;
	}

	@Subscribe
	public Result<CalculatorRegistered> onMeasurementSpecification(final MeasurementSpecificationVisited m) {
		final MeasurementSpecification spec = m.getEntity();
		final MeasuringPoint measuringPoint = spec.getMonitor().getMeasuringPoint();
		if (measuringPoint instanceof AssemblyPassiveResourceMeasuringPoint) {
			final AssemblyPassiveResourceMeasuringPoint passiveResourceMeasuringPoint = (AssemblyPassiveResourceMeasuringPoint) measuringPoint;

			if (MetricDescriptionUtility.metricDescriptionIdsEqual(spec.getMetricDescription(),
					MetricDescriptionConstants.WAITING_TIME_METRIC)) {
				final Calculator calculator = this.table.setupWaitingTimeCalculator(passiveResourceMeasuringPoint,
						this.calculatorFactory);
				return Result.of(new CalculatorRegistered(calculator));
			} else if (MetricDescriptionUtility.metricDescriptionIdsEqual(spec.getMetricDescription(),
					MetricDescriptionConstants.HOLDING_TIME_METRIC)) {
				final Calculator calculator = this.table.setupHoldingTimeCalculator(passiveResourceMeasuringPoint,
						this.calculatorFactory);
				return Result.of(new CalculatorRegistered(calculator));
			} else if (MetricDescriptionUtility.metricDescriptionIdsEqual(spec.getMetricDescription(),
					MetricDescriptionConstants.STATE_OF_PASSIVE_RESOURCE_METRIC)) {
				final Calculator calculator = this.table
						.setupStateOfPassiveResourceCalculator(passiveResourceMeasuringPoint, this.calculatorFactory);
				return Result.of(new CalculatorRegistered(calculator));
			}
		}
		return Result.empty();

	}

	@Subscribe
	public Result<ProbeTaken> onResourceDemandRequest(final ResourceDemandRequested resourceDemandRequest) {
		if (resourceDemandRequest.getEntity().getResourceType() == ResourceType.PASSIVE) {
			final Probe probe = this.table.currentTimeOfPassiveResourceRequested(resourceDemandRequest);
			return Result.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(probe).build()));
		}

		return Result.empty();
	}

	@Subscribe
	public Result<ProbeTaken> onPassiveResourceAcquired(final PassiveResourceAcquired passiveResourceAcquired) {
		if (passiveResourceAcquired.getEntity().getResourceType() == ResourceType.PASSIVE) {
			final Probe probe = this.table.currentTimeOfPassiveResourceAcquired(passiveResourceAcquired);
			return Result.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(probe).build()));
		}

		return Result.empty();
	}

	@Subscribe
	public Result<ProbeTaken> onPassiveResourceReleased(final PassiveResourceReleased passiveResourceReleased){
		if(passiveResourceReleased.getEntity().getResourceType() == ResourceType.PASSIVE) {
			final Probe probe = this.table.currentTimeOfPassiveResourceReleased(passiveResourceReleased);
			return Result.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(probe).build()));
		}
		return Result.empty();
	}

	@Subscribe
	public Result<ProbeTaken> onPassiveResourceStateUpdated(final PassiveResourceStateUpdated stateUpdated) {
		if (stateUpdated.getEntity().getResourceType() == ResourceType.PASSIVE) {
			final Probe probe = this.table.stateOfPassiveResourceUpdated(stateUpdated);
			return Result.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(probe).build()));
		}

		return Result.empty();
	}

}
