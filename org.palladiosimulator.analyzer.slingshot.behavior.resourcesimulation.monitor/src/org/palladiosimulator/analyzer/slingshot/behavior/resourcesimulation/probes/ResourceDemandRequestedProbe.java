package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.probes;

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ActiveResourceStateUpdated;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventBasedListProbe;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventDistinguisher;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

/**
 *
 * BEWARE!!!! "Specifies a (resource demand, point in time)-tuple," i.e. the
 * TUPLE IS FUCKING UPSIDE DOWN COMPARED TO OTHERS!!!
 *
 * AS RESULT THE VALUE AND TIME OPERATIONS OF THE PROBE ARE IN FACT SWITCHED UP,
 * TOO!
 *
 * @author stiesssh
 *
 */
public final class ResourceDemandRequestedProbe extends EventBasedListProbe<Double, Duration> {

	/**
	 *
	 */
	public ResourceDemandRequestedProbe() {
		super(MetricDescriptionConstants.RESOURCE_DEMAND_METRIC_TUPLE);
		// yes, the unary calculator need the Tuple in the probe because it does an "isCompatibleWith" between calculator (metric entity) and probe metric description...
	}

	/**
	 * Constructs an EventDimensionlessProbe with a custom distinguisher.
	 *
	 * @param distinguisher The distinguisher that instantiates a
	 *                      {@link RequestContext}.
	 */
	public ResourceDemandRequestedProbe(
			final EventDistinguisher distinguisher) {
		super(MetricDescriptionConstants.RESOURCE_DEMAND_METRIC_TUPLE, distinguisher);
	}

	@Override
	public Measure<Double, Duration> getMeasurement(final DESEvent event) {
		return Measure.valueOf(event.time(), SI.SECOND);
	}

	@Override
	public Measure<Double, Duration> getTime(final DESEvent event) {
		if (event instanceof ActiveResourceStateUpdated) {
			return Measure.valueOf(((ActiveResourceStateUpdated) event).getResourceDemandRequested(), SI.SECOND);
		}
		throw new IllegalArgumentException("event not of type ACTIVERESOUCRESTATEUPDATED");
	}
}
