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
 * Probe for the Request arrival rate for some operation.
 *
 * The Request arrival rate is smoothed using an exponential moving average (with static parameters
 * given below) as it can be based on some probabilistic function and thus be quite noisy without
 * smoothing
 *
 * @author Jens Berberich
 *
 */
public final class RequestArrivalRateProbe extends EventBasedListProbe<Double, Frequency> {

    private double time;
    private double rate;
    private static final double alpha = 0.01;
    private double currentRate;
    private static final double windowSizeInSeconds = 120;
    private int nextFactor = 1;

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
            double deltaTime = event.time() - this.time;

            if (deltaTime != 0) {
                // Update the EMA with the new data point
                this.currentRate = (1.0 - this.alpha) * this.currentRate + this.nextFactor * (this.alpha / deltaTime);
                this.nextFactor = 1;
                double previousRate = this.currentRate;
            } else {
                this.nextFactor += 1;
            }

            // Adjust the time-weighted factor based on the time elapsed since the last
            // update
            final double timeFactor = Math.exp(-deltaTime / this.windowSizeInSeconds);

            // Apply the time factor to the current value
            this.currentRate *= timeFactor;

            // Update the last update time
            this.time = event.time();

            return Measure.valueOf(this.currentRate, SI.HERTZ);
        }
        throw new IllegalArgumentException(String.format("Wrong eventype. Expected %s but got %s.",
                ActiveResourceStateUpdated.class.getSimpleName(), event.getClass()
                    .getSimpleName()));
    }
}
