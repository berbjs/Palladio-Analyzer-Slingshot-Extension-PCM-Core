package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.probes;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.PassiveResourceStateUpdated;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventBasedListProbe;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

/**
 *
 * @author Sarah Stieß
 *
 */
public final class StateOfPassiveResourceProbe extends EventBasedListProbe<Long, Dimensionless> {

	/**
	 * Constructs a StateOfActiveResourceProbe.
	 */
	public StateOfPassiveResourceProbe() {
		super(MetricDescriptionConstants.STATE_OF_PASSIVE_RESOURCE_METRIC_TUPLE);
	}

	@Override
	public Measure<Long, Dimensionless> getMeasurement(final DESEvent event) {
		if (event instanceof PassiveResourceStateUpdated) {
			return Measure.valueOf(((PassiveResourceStateUpdated) event).freeTokens(), Dimensionless.UNIT);
		}
		throw new IllegalArgumentException(String.format("Wrong eventype. Expected %s but got %s.",
				PassiveResourceStateUpdated.class.getSimpleName(), event.getClass().getSimpleName()));
	}
}
