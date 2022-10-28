package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.monitor;

import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest.ResourceType;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.PassiveResourceAcquired;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.ResourceDemandRequested;
import org.palladiosimulator.analyzer.slingshot.monitor.data.CalculatorRegistered;
import org.palladiosimulator.analyzer.slingshot.monitor.data.MonitorModelVisited;
import org.palladiosimulator.analyzer.slingshot.monitor.data.ProbeTaken;
import org.palladiosimulator.analyzer.slingshot.monitor.data.ProbeTakenEntity;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.Reified;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.pcm.repository.PassiveResource;
import org.palladiosimulator.pcmmeasuringpoint.AssemblyPassiveResourceMeasuringPoint;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.probeframework.probes.Probe;

import com.google.common.eventbus.Subscribe;

@OnEvent(when = MonitorModelVisited.class, whenReified = MeasurementSpecification.class, then = CalculatorRegistered.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = ResourceDemandRequested.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = PassiveResourceAcquired.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
public class PassiveResourceMonitorInitializationBehavior implements SimulationBehaviorExtension {

	private final IGenericCalculatorFactory calculatorFactory;

	private final PassiveResourceProbeTable table = new PassiveResourceProbeTable();

	@Inject
	public PassiveResourceMonitorInitializationBehavior(final IGenericCalculatorFactory calculatorFactory) {
		this.calculatorFactory = calculatorFactory;
	}

	@Subscribe
	public ResultEvent<CalculatorRegistered> onMeasurementSpecification(
			@Reified(MeasurementSpecification.class) final MonitorModelVisited<MeasurementSpecification> m) {
		final MeasurementSpecification spec = m.getModelElement();

		if (MetricDescriptionUtility.metricDescriptionIdsEqual(spec.getMetricDescription(),
				MetricDescriptionConstants.STATE_OF_PASSIVE_RESOURCE_METRIC)) {
			return this.setupPassiveResourceStateCalculator(spec);
		}

		return ResultEvent.empty();
	}

	private ResultEvent<CalculatorRegistered> setupPassiveResourceStateCalculator(final MeasurementSpecification spec) {
		final MeasuringPoint measuringPoint = spec.getMonitor().getMeasuringPoint();
		if (measuringPoint instanceof AssemblyPassiveResourceMeasuringPoint) {
			final AssemblyPassiveResourceMeasuringPoint passiveResourceMeasuringPoint = (AssemblyPassiveResourceMeasuringPoint) measuringPoint;
			final PassiveResource passiveResource = passiveResourceMeasuringPoint.getPassiveResource();
			this.table.addPassiveResource(passiveResource);

			if (MetricDescriptionUtility.metricDescriptionIdsEqual(spec.getMetricDescription(),
					MetricDescriptionConstants.WAITING_TIME_METRIC)) {
				final Calculator calculator = this.table.setupWaitingTimeCalculator(passiveResourceMeasuringPoint,
						this.calculatorFactory);
				return ResultEvent.of(new CalculatorRegistered(calculator));
			}
		}
		return ResultEvent.empty();
	}

	@Subscribe
	public ResultEvent<ProbeTaken> onResourceDemandRequest(final ResourceDemandRequested resourceDemandRequest) {
		if (resourceDemandRequest.getEntity().getResourceType() == ResourceType.PASSIVE) {
			final Probe probe = this.table.currentTimeOfPassiveResourceRequested(resourceDemandRequest);
			return ResultEvent.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(probe).build()));
		}

		return ResultEvent.empty();
	}

	@Subscribe
	public ResultEvent<ProbeTaken> onPassiveResourceAcquired(final PassiveResourceAcquired passiveResourceAcquired) {
		if (passiveResourceAcquired.getEntity().getResourceType() == ResourceType.PASSIVE) {
			final Probe probe = this.table.currentTimeOfPassiveResourceAcquired(passiveResourceAcquired);
			return ResultEvent.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(probe).build()));
		}

		return ResultEvent.empty();
	}
}
