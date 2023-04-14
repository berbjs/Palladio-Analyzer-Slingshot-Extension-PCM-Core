package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.monitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ActiveResourceStateUpdated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ResourceDemandCalculated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.probes.ResourceDemandRequestedProbe;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.probes.StateOfActiveResourceProbe;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcmmeasuringpoint.ActiveResourceMeasuringPoint;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.DefaultCalculatorProbeSets;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.probeframework.probes.Probe;

/**
 *
 * Creates Probes and Calculators and maps Resources to their Probes.
 *
 * TODO i think it's smarter to map by processing resource specification, as one
 * resource container may contain multiple specs.
 *
 * @author Julijan Katic, Floriment Klinaku, Sarah Stieß
 *
 */
public final class ActiveResourceProbeTable {

	private final Map<String, Probes> probes = new HashMap<>();

	public void addActiveResource(final ResourceContainer activeResource) {
		this.probes.putIfAbsent(activeResource.getId(), new Probes());
	}

	public Optional<Probe> currentStateOfActiveResource(final ActiveResourceStateUpdated event) {
		final Probes probes = this.probes
				.get(event.getEntity().getAllocationContext().getResourceContainer_AllocationContext().getId());// TODO
		if (probes != null) {
			probes.stateOfActiveResourceProbe.takeMeasurement(event);
			return Optional.of(probes.stateOfActiveResourceProbe);
		}
		return Optional.empty();
	}

	public Optional<Probe> currentResourceDemand(final ResourceDemandCalculated event) {
		final Probes probes = this.probes
				.get(event.getEntity().getAllocationContext().getResourceContainer_AllocationContext().getId());// TODO
		if (probes != null) {
			probes.resourceDemandProbe.takeMeasurement(event);
			return Optional.of(probes.resourceDemandProbe);
		}
		return Optional.empty();
	}

	public Calculator setupStateOfActiveResourceCalculator(final ActiveResourceMeasuringPoint measuringPoint,
			final IGenericCalculatorFactory calculatorFactory) {
		this.addActiveResource(
				measuringPoint.getActiveResource().getResourceContainer_ProcessingResourceSpecification());
		final Probes probes = this.probes.get(measuringPoint.getActiveResource().getResourceContainer_ProcessingResourceSpecification().getId());
		return calculatorFactory.buildCalculator(MetricDescriptionConstants.STATE_OF_ACTIVE_RESOURCE_METRIC_TUPLE,
				measuringPoint,
				DefaultCalculatorProbeSets.createSingularProbeConfiguration(probes.stateOfActiveResourceProbe));
	}

	public Calculator setupResoucreDemandCalculator(final ActiveResourceMeasuringPoint measuringPoint,
			final IGenericCalculatorFactory calculatorFactory) {
		this.addActiveResource(
				measuringPoint.getActiveResource().getResourceContainer_ProcessingResourceSpecification());
		final Probes probes = this.probes
				.get(measuringPoint.getActiveResource().getResourceContainer_ProcessingResourceSpecification().getId());
		return calculatorFactory.buildCalculator(MetricDescriptionConstants.RESOURCE_DEMAND_METRIC_TUPLE,
				measuringPoint,
				DefaultCalculatorProbeSets.createSingularProbeConfiguration(probes.resourceDemandProbe));
	}

	/**
	 *
	 * Probes for an Active resource. They need no distinguisher, because the unary
	 * calculators need not match probes.
	 *
	 * @author Sarah Stieß
	 *
	 */
	private static final class Probes {
		private final StateOfActiveResourceProbe stateOfActiveResourceProbe = new StateOfActiveResourceProbe();
		private final ResourceDemandRequestedProbe resourceDemandProbe = new ResourceDemandRequestedProbe();
	}

}
