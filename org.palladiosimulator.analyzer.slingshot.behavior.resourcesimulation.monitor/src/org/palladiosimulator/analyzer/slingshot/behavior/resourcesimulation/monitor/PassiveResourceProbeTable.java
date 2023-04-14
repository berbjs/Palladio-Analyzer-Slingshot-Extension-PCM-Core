package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.monitor;

import java.util.HashMap;
import java.util.Map;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.PassiveResourceAcquired;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.PassiveResourceReleased;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.ResourceDemandRequested;
import org.palladiosimulator.analyzer.slingshot.monitor.utils.probes.EventCurrentSimulationTimeProbe;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.pcm.repository.PassiveResource;
import org.palladiosimulator.pcmmeasuringpoint.AssemblyPassiveResourceMeasuringPoint;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.DefaultCalculatorProbeSets;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.probeframework.measurement.RequestContext;
import org.palladiosimulator.probeframework.probes.Probe;


/**
 * @author Julijan Katic, Floriment Klinaku, Sarah Stiess
 *
 */
public final class PassiveResourceProbeTable {

	private final Map<String, Probes> probes = new HashMap<>();

	public void addPassiveResource(final PassiveResource passiveResource) {
		this.probes.putIfAbsent(passiveResource.getId(), new Probes());
	}

	public Probe currentTimeOfPassiveResourceRequested(final ResourceDemandRequested requested) {
		final PassiveResource passiveResource = requested.getEntity().getPassiveResource().orElseThrow();
		final Probes probes = this.probes.get(passiveResource.getId());
		if (probes != null) {
			probes.resourceDemandRequestedProbe.takeMeasurement(requested);
			return probes.resourceDemandRequestedProbe;
		}
		return null;
	}

	public Probe currentTimeOfPassiveResourceAcquired(final PassiveResourceAcquired passiveResourceAcquired) {
		final PassiveResource passiveResource = passiveResourceAcquired.getEntity().getPassiveResource().orElseThrow();
		final Probes probes = this.probes.get(passiveResource.getId());
		if (probes != null) {
			probes.passiveResourceAcquiredProbe.takeMeasurement(passiveResourceAcquired);
			return probes.passiveResourceAcquiredProbe;
		}
		return null;
	}

	public Probe currentTimeOfPassiveResourceReleased(final PassiveResourceReleased passiveResourceReleased) {
		final PassiveResource passiveResource = passiveResourceReleased.getEntity().getPassiveResource().orElseThrow();
		final Probes probes = this.probes.get(passiveResource.getId());
		if (probes != null) {
			probes.passiveResourceReleasedProbe.takeMeasurement(passiveResourceReleased);
			return probes.passiveResourceAcquiredProbe;
		}
		return null;
	}


	public Calculator setupWaitingTimeCalculator(final AssemblyPassiveResourceMeasuringPoint measuringPoint,
			final IGenericCalculatorFactory calculatorFactory) {
		this.addPassiveResource(measuringPoint.getPassiveResource());
		final Probes probes = this.probes.get(measuringPoint.getPassiveResource().getId());
		return calculatorFactory.buildCalculator(MetricDescriptionConstants.WAITING_TIME_METRIC_TUPLE, measuringPoint,
				DefaultCalculatorProbeSets.createStartStopProbeConfiguration(probes.resourceDemandRequestedProbe,
						probes.passiveResourceAcquiredProbe));
	}

	public Calculator setupHoldingTimeCalculator(final AssemblyPassiveResourceMeasuringPoint measuringPoint,
			final IGenericCalculatorFactory calculatorFactory) {
		this.addPassiveResource(measuringPoint.getPassiveResource());
		final Probes probes = this.probes.get(measuringPoint.getPassiveResource().getId());
		return calculatorFactory.buildCalculator(MetricDescriptionConstants.HOLDING_TIME_METRIC_TUPLE, measuringPoint,
				DefaultCalculatorProbeSets.createStartStopProbeConfiguration(probes.passiveResourceAcquiredProbe,
						probes.passiveResourceReleasedProbe));
	}

	private static final class Probes {
		private final EventCurrentSimulationTimeProbe resourceDemandRequestedProbe = new EventCurrentSimulationTimeProbe(t -> new RequestContext(((ResourceDemandRequested) t).getEntity().getSeffInterpretationContext().getRequestProcessingContext().getUserId()));
		private final EventCurrentSimulationTimeProbe passiveResourceAcquiredProbe = new EventCurrentSimulationTimeProbe(t -> new RequestContext(((PassiveResourceAcquired) t).getEntity().getSeffInterpretationContext().getRequestProcessingContext().getUserId()));
		private final EventCurrentSimulationTimeProbe passiveResourceReleasedProbe = new EventCurrentSimulationTimeProbe(t -> new RequestContext(((PassiveResourceReleased) t).getEntity().getSeffInterpretationContext().getRequestProcessingContext().getUserId()));
	}





}
