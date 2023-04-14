package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.probes;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ActiveResourceStateUpdated;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventBasedListProbe;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

/**
 *
 * @author Sarah Stieß
 *
 */
public final class StateOfActiveResourceProbe extends EventBasedListProbe<Long, Dimensionless> {

	/**
	 * Constructs a StateOfActiveResourceProbe.
	 */
	public StateOfActiveResourceProbe() {
		super(MetricDescriptionConstants.STATE_OF_ACTIVE_RESOURCE_METRIC_TUPLE);
	}

	@Override
	public Measure<Long, Dimensionless> getMeasurement(final DESEvent event) {
		if (event instanceof ActiveResourceStateUpdated) {
			return Measure.valueOf(((ActiveResourceStateUpdated) event).requestsAtResource(), Dimensionless.UNIT);
		}
		throw new IllegalArgumentException(String.format("Wrong eventype. Expected %s but got %s.",
				ActiveResourceStateUpdated.class.getSimpleName(), event.getClass().getSimpleName()));
	}
}
