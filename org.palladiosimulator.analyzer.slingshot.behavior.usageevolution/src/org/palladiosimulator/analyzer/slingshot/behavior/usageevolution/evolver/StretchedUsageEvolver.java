package org.palladiosimulator.analyzer.slingshot.behavior.usageevolution.evolver;

import java.util.Optional;

import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.scaledl.usageevolution.Usage;

import tools.descartes.dlim.generator.ModelEvaluator;

public class StretchedUsageEvolver extends AbstractUsageEvolver {

	// Stretching factor.
	private final double timeFactor;

	// Value subtracted for evaluation of last interval
	private final static double DELTA = 0.000001;

	/**
	 * Creates the stretching usage evolver.
	 *
	 * @param rtState         The SimuLizar runtime state.
	 * @param firstOccurrence The first point in time at which the usage evolution
	 *                        should be executed.
	 * @param delay           The repeating interval in which usage evolution should
	 *                        be executed.
	 * @param evolvedScenario The scenario evolved by <code>this</code>.
	 */
	public StretchedUsageEvolver(final UsageScenario evolvedScenario, final Usage usage,
			final Optional<Double> maxSimTime) {
		super(evolvedScenario, usage);
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
