package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.probes;

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ResourceDemandCalculated;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventBasedListProbe;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

/**
 *
 * BEWARE!!! This probe interchanges the implementation of getMeasurement and
 * getTime, because
 * {@link MetricDescriptionConstants.RESOURCE_DEMAND_METRIC_TUPLE} specifies the
 * resource demand as a "[..] (resource demand, point in time)-tuple, [..]",
 * which is upside down compared to all other metric set descriptions.
 *
 * @author Sarah Stieß
 *
 */
public final class ResourceDemandRequestedProbe extends EventBasedListProbe<Double, Duration> {

	/**
	 * Constructs a ResourceDemandRequestedProbe.
	 */
	public ResourceDemandRequestedProbe() {
		super(MetricDescriptionConstants.RESOURCE_DEMAND_METRIC_TUPLE);
	}

	/**
	 * Is actually getTime
	 */
	@Override
	public Measure<Double, Duration> getMeasurement(final DESEvent event) {
		return Measure.valueOf(event.time(), SI.SECOND);
	}

	/**
	 * Is actually getMeasurement
	 */
	@Override
	public Measure<Double, Duration> getTime(final DESEvent event) {
		if (event instanceof ResourceDemandCalculated) {
			return Measure.valueOf(((ResourceDemandCalculated) event).getResourceDemandRequested(), SI.SECOND);
		}
		throw new IllegalArgumentException(String.format("Wrong eventype. Expected %s but got %s.",
				ResourceDemandCalculated.class.getSimpleName(), event.getClass().getSimpleName()));
	}
}
