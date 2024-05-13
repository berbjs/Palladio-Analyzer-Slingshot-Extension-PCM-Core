package org.palladiosimulator.analyzer.slingshot.behavior.usageevolution.evolver;

import java.util.Optional;

import org.scaledl.usageevolution.Usage;

import tools.descartes.dlim.generator.ModelEvaluator;

/**
 *
 * Usage evolver that stretches the DLIM to the entire Simulation time.
 *
 * Based on
 * {@code org.palladiosimulator.simulizar.usagemodel.StretchedUsageEvolver}
 *
 * @author Sarah Stieß
 *
 */
public class StretchedUsageEvolver extends AbstractUsageEvolver {

	/** Stretching factor. */
	private final double timeFactor;

	/** Value subtracted for evaluation of last interval */
	private final static double DELTA = 0.000001;

	/**
	 * Creates a stretching usage evolver.
	 *
	 * @param usage      the usage to be evolved.
	 * @param maxSimTime duration to stretch up to. Must not be empty.
	 */
	public StretchedUsageEvolver(final Usage usage, final Optional<Double> maxSimTime) {
		super(usage);
		if (maxSimTime.isEmpty()) {
			throw new IllegalArgumentException(
					"Initializing a streched usage evolver requires a specified maximum simulation time");
		}
		this.timeFactor = maxSimTime.get() / this.getDLIMFinalDuration();
	}

	@Override
	protected double getNewRate(final ModelEvaluator loadEvaluator, final double time) {
		final double evaluationTime = time / this.timeFactor;
		if (evaluationTime >= this.getDLIMFinalDuration() - DELTA) {
			// The LIMBO evaluator do not define a value at the total duration
			// time, so get a value close to end of the simulation by requesting
			// the value one millionth of a time unit before the total duration
			return loadEvaluator.getArrivalRateAtTime(evaluationTime - DELTA);
		}
		return loadEvaluator.getArrivalRateAtTime(time / this.timeFactor);
	}

}
