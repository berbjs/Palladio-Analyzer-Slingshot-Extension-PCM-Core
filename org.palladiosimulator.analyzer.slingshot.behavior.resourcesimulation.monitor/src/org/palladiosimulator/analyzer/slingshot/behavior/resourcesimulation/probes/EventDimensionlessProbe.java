package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.probes;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ActiveResourceStateUpdated;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventBasedListProbe;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventDistinguisher;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

/**
 *
 * @author stiesssh
 *
 */
public final class EventDimensionlessProbe extends EventBasedListProbe<Long, Dimensionless> {

	/**
	 *
	 */
	public EventDimensionlessProbe() {
		super(MetricDescriptionConstants.STATE_OF_ACTIVE_RESOURCE_METRIC_TUPLE);
		// yes, the unary calculator need the Tuple in the probe because it does an "isCompatibleWith" between calculator (metric entity) and probe metric description...
	}

	/**
	 * Constructs an EventDimensionlessProbe with a custom distinguisher.
	 *
	 * @param distinguisher The distinguisher that instantiates a
	 *                      {@link RequestContext}.
	 */
	public EventDimensionlessProbe(
			final EventDistinguisher distinguisher) {
		super(MetricDescriptionConstants.STATE_OF_ACTIVE_RESOURCE_METRIC_TUPLE, distinguisher);
	}

	@Override
	public Measure<Long, Dimensionless> getMeasurement(final DESEvent event) {
		if (event instanceof ActiveResourceStateUpdated) {
			return Measure.valueOf(((ActiveResourceStateUpdated)event).getQueueLength(), Dimensionless.UNIT);
		}
		throw new IllegalArgumentException("event not of type ACTIVERESOUCRESTATEUPDATED");
	}

//	@Override
//	protected ProbeMeasurement getProbeMeasurement(final DESEvent event) {
//		final List<MeasuringValue> list = new ArrayList<>(2);
//
//		final BasicMeasurement<Double, Duration> timeMeasurement = new BasicMeasurement<>(
//				this.getTime(event), MetricDescriptionConstants.POINT_IN_TIME_METRIC);
//
//        final MeasuringValue pointInTimeMeasurement = timeMeasurement.getMeasuringValueForMetric(POINT_IN_TIME_METRIC);
//		list.add(pointInTimeMeasurement);/// time
//
//
//		final BasicMeasurement<Long, Dimensionless> valueMeasurement = new BasicMeasurement<>(
//				this.getMeasurement(event), MetricDescriptionConstants.STATE_OF_ACTIVE_RESOURCE_METRIC);
//
//        final MeasuringValue valueFooMeasurement = valueMeasurement.getMeasuringValueForMetric(MetricDescriptionConstants.STATE_OF_ACTIVE_RESOURCE_METRIC);
//		list.add(valueFooMeasurement);/// value
//
//
//		final MeasurementListMeasureProvider resultMeasurement = new MeasurementListMeasureProvider(list);
//
//		return new ProbeMeasurement(resultMeasurement, this, this.getDistinguisher().apply(event));
//	}

}
