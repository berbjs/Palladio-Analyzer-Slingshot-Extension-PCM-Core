package org.palladiosimulator.analyzer.slingshot.behavior.usageevolution.evolver;

import org.apache.log4j.Logger;
import org.scaledl.usageevolution.Usage;

import tools.descartes.dlim.generator.ModelEvaluator;

/**
 *
 * Usage evolver that assumes that the time unit of the DLIM sequence and
 * simulation are equivalent. Repeats the DLIM sequence once its end has been
 * reached.
 *
 * Based on
 * {@code org.palladiosimulator.simulizar.usagemodel.LoopingUsageEvolver}
 *
 * @author Sarah Stieß
 *
 */
public class LoopingUsageEvolver extends AbstractUsageEvolver {

	static final Logger LOGGER = Logger.getLogger(LoopingUsageEvolver.class);

	/**
	 * Constructs a looping usage evolver.
	 *
	 * @param usage the usage to be evolved.
	 */
	public LoopingUsageEvolver(final Usage usage) {
		super(usage);
		if (!usage.isRepeatingPattern()) {
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
