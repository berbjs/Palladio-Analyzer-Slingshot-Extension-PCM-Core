package org.palladiosimulator.analyzer.slingshot.behavior.usageevolution.evolver;

import org.apache.log4j.Logger;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.scaledl.usageevolution.Usage;

import tools.descartes.dlim.generator.ModelEvaluator;

/**
 * Looping usage evolver. Assumes that the time unit of the DLIM sequence and
 * simulation are equivalent. Repeats the DLIM sequence once its end has been
 * reached.
 *
 * @author stier
 *
 */
public class LoopingUsageEvolver extends AbstractUsageEvolver {

	static final Logger LOGGER = Logger.getLogger(LoopingUsageEvolver.class);

	// Assumed this is an offset from previous iteration, but that makes no sense
	// because that is already covered with the floor mod. */
	// private double simulationTimeOffset;

	/**
	 * Constructs the looping usage evolver.
	 *
	 * @param rtState         SimuLizar runtime state.
	 * @param firstOccurrence First point in time at which the evolver should evolve
	 *                        the load.
	 * @param delay           The interval in which the evolver should evolve the
	 *                        load.
	 * @param evolvedScenario The evolved scenario.
	 *
	 */
	public LoopingUsageEvolver(final UsageScenario evolvedScenario, final Usage usage) {
		super(evolvedScenario, usage);
		if (!this.getCorrespondingUsage().isRepeatingPattern()) {
			throw new IllegalArgumentException("The corresponding usage model must contain a repeating pattern.");
		}
	}

	@Override
	protected double getNewRate(final ModelEvaluator evaluator, final double time) {
		return evaluator.getArrivalRateAtTime(floorMod(time, this.getDLIMFinalDuration()));
	}

	/**
	 * Calculates the modulo between floored x and y.
	 *
	 * @param x The dividend.
	 * @param y The divisor.
	 * @return Floored modulo.
	 */
	private static double floorMod(final double x, final double y) {
		return x - Math.floor(x / y) * y;
	}
}
