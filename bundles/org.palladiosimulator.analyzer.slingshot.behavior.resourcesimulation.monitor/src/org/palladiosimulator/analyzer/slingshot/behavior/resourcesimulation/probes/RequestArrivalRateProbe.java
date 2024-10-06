package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.probes;

import javax.measure.Measure;
import javax.measure.quantity.Frequency;
import javax.measure.unit.SI;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ActiveResourceStateUpdated;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFModelPassedElement;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventBasedListProbe;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

/**
 * Probe for the Number of Elements in a Elastic Infrastructure.
 *
 * The Number of Elements is always calculated with regard to a certain target group configuration,
 * i.e. only elements of a given target group are aconsidered.
 *
 * @author Sarah Stieß
 *
 */
public final class RequestArrivalRateProbe extends EventBasedListProbe<Double, Frequency> {

    private double time;
    private double rate;

    /**
     * Constructor for RequestArrivalRateProbe.
     *
     * @param elasticInfrastructureConfiguration
     *            configuration of target group that will be measured.
     */
    public RequestArrivalRateProbe() {
        super(MetricDescriptionConstants.REQUEST_ARRIVAL_RATE_TUPLE);
        this.time = 0;
    }

    @Override
    public Measure<Double, Frequency> getMeasurement(final DESEvent event) {
        if (event instanceof SEFFModelPassedElement) {
            if (this.time != event.time()) {
                this.rate = 1 / (event.time() - this.time);
                this.time = event.time();
                return Measure.valueOf(rate, SI.HERTZ);
            } else {
                this.rate *= 2;
            }
            return Measure.valueOf(this.rate, SI.HERTZ);
        }
        throw new IllegalArgumentException(String.format("Wrong eventype. Expected %s but got %s.",
                ActiveResourceStateUpdated.class.getSimpleName(), event.getClass()
                    .getSimpleName()));
    }
}
