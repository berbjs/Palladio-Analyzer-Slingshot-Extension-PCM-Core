package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.monitor;

import java.util.HashMap;
import java.util.Map;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ActiveResourceStateUpdated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.probes.EventDimensionlessProbe;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcmmeasuringpoint.ActiveResourceMeasuringPoint;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.DefaultCalculatorProbeSets;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.probeframework.probes.Probe;

/**
 * @author Julijan Katic, Floriment Klinaku, Sarah Stiess
 *
 */
public final class ActiveResourceProbeTable {

	private final Map<String, Probes> probes = new HashMap<>();

	public void addActiveResource(final ResourceContainer activeResource) {
		this.probes.putIfAbsent(activeResource.getId(), new Probes());
	}

	public Probe currentStateOfActiveResource(final ActiveResourceStateUpdated event) {
		final Probes probes = this.probes.get(event.getEntity().getAllocationContext().getResourceContainer_AllocationContext().getId());// TODO
		if (probes != null) {
			probes.probe.takeMeasurement(event);
			return probes.probe;
		}
		return null;
	}

	public Calculator setupStateOfActiveResourceCalculator(final ActiveResourceMeasuringPoint measuringPoint,
			final IGenericCalculatorFactory calculatorFactory) {
		final Probes probes = this.probes.get(measuringPoint.getActiveResource().getResourceContainer_ProcessingResourceSpecification().getId());
		return calculatorFactory.buildCalculator(MetricDescriptionConstants.STATE_OF_ACTIVE_RESOURCE_METRIC_TUPLE,
				measuringPoint, DefaultCalculatorProbeSets.createSingularProbeConfiguration(probes.probe));
	}

	private static final class Probes {
		private final EventDimensionlessProbe probe = new EventDimensionlessProbe();
		// no distinguisher required, because unary calculators put into and remove from
		// the calculator in one go, i.e. no need for proper matching.
	}

}
