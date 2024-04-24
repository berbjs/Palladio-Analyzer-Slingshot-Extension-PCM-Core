package org.palladiosimulator.analyzer.slingshot.behavior.usageevolution.evolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.parameter.VariableCharacterisation;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.pcm.usagemodel.Workload;
import org.scaledl.usageevolution.Usage;
import org.scaledl.usageevolution.WorkParameterEvolution;

import tools.descartes.dlim.Sequence;
import tools.descartes.dlim.generator.ModelEvaluator;

/**
 * Usage evolver which updates the workload according to a Usage Evolution
 * model.
 *
 * Based on
 * {@code org.palladiosimulator.simulizar.usagemodel.PeriodicallyTriggeredUsageEvolver}
 *
 * @author Sarah Stieß
 *
 */
public abstract class AbstractUsageEvolver {

	static final Logger LOGGER = Logger.getLogger(AbstractUsageEvolver.class);

	private final Optional<ModelEvaluator> loadEvaluator;
	private final Map<VariableCharacterisation, ModelEvaluator> workEvaluators = new HashMap<VariableCharacterisation, ModelEvaluator>();

	private final Usage usage;


	/**
	 * Constructs a usage evolver.
	 *
	 * @param usage the usage to be evolved. Must not be null.
	 */
	public AbstractUsageEvolver(final Usage usage) {
		if (usage == null) {
			throw new IllegalArgumentException("Usage is null, but must not be.");
		}

		this.usage = usage;

		final Sequence loadEvolutionSequence = usage.getLoadEvolution();
		if (loadEvolutionSequence != null) {
			this.loadEvaluator = Optional.of(new ModelEvaluator(loadEvolutionSequence));
		} else {
			this.loadEvaluator = Optional.empty();
		}

		initWorkEvaluators();

	}

	/**
	 * Creates {@code ModelEvaluator}s
	 */
	protected void initWorkEvaluators() {

		if (usage.getWorkEvolutions() != null) {

				for (final WorkParameterEvolution workParam : usage.getWorkEvolutions()) {
					final VariableCharacterisation varChar = workParam.getVariableCharacterisation();
					final Sequence paramSequence = workParam.getEvolution();
					if (varChar == null) {
						LOGGER.error("Skipping evolution of unspecified work parameter");
						continue;
					}
					if (paramSequence == null) {
						LOGGER.error("Skipping unspecified evolution for work parameter " + varChar);
						continue;
					}

					// Add parameter and model evaluator for the work parameter
					workEvaluators.put(varChar, new ModelEvaluator(paramSequence));
				}
			}

	}

	/**
	 * Actually evolves the load.
	 */
	public void triggerInternal(final double time) {

		loadEvaluator.ifPresent(evaluator -> this.evolveLoad(evaluator, time));

		// Then, iterate through work parameters to evolve
		for (final VariableCharacterisation workParam : this.workEvaluators.keySet()) {
			this.evolveWork(workParam, workEvaluators.get(workParam), time);
		}
	}

	/**
	 * The length of <code>this</code>' DLIM sequence.
	 *
	 * @return The length of <code>this</code>' DLIM sequence.
	 */
	protected double getDLIMFinalDuration() {
		return this.usage.getLoadEvolution().getFinalDuration();
	}

	/**
	 * Evolve the load.
	 *
	 * @param loadEvaluator DLIM evaluator used to fetch the load at the current
	 *                      point in time.
	 */
	protected void evolveLoad(final ModelEvaluator loadEvaluator, final double time) {

		double newRate = this.getNewRate(loadEvaluator, time);
		final Workload wl = this.usage.getScenario().getWorkload_UsageScenario();
		if (wl != null) {
			if (wl instanceof OpenWorkload) {
			    	double interArrivalTime = Integer.MAX_VALUE;
				final PCMRandomVariable openwl = ((OpenWorkload) wl).getInterArrivalTime_OpenWorkload();
				if (newRate != 0) {
					// Using inverse value to convert from arrival rate to inter arrival
					// time
					interArrivalTime = 1 / newRate;
				}

				final String interArrivalTimeNewSpec = Double.toString(interArrivalTime);
				if (interArrivalTimeNewSpec.equals(openwl.getSpecification())) {
					LOGGER.debug("Inter arrival time is still: " + interArrivalTimeNewSpec);
				} else {
					LOGGER.debug(
							"Changing inter arrival time from: " + openwl.getSpecification() + " to :" + interArrivalTimeNewSpec);
					openwl.setSpecification(interArrivalTimeNewSpec);
				}
			} else if (wl instanceof ClosedWorkload) {
				final int newRateInt = (int) Math.round(newRate);
				final int oldRate = ((ClosedWorkload) wl).getPopulation();
				if (newRateInt == oldRate) {
					LOGGER.debug("Closed workload population is still: " + newRateInt);
				} else {
					LOGGER.debug("Changing closed workload population from: " + oldRate + " to " + newRateInt);
					((ClosedWorkload) wl).setPopulation(newRateInt);
				}
			}
		}
	}

	/**
	 * Get the new rate of the Usage or workload parameter characterization.
	 *
	 * @param loadEvaluator The DLIM evaluator used for the evaluation.
	 * @return The new Usage or workload parameter characterization.
	 */
	protected abstract double getNewRate(final ModelEvaluator loadEvaluator, final double time);

	/**
	 * Evolves a workload parameter.
	 *
	 * @param workParameter The evolved parameter.
	 * @param evaluator     The evaluator used for evaluating DLIM sequence.
	 */
	protected void evolveWork(final VariableCharacterisation workParameter, final ModelEvaluator evaluator,
			final double time) {
		if (evaluator == null) {
			return;
		}

		// Support only long values for now
		final long newRate = Math.round(this.getNewRate(evaluator, time));
		final String newRateStr = Long.toString(newRate);

		LOGGER.debug("Changing work from "
				+ workParameter.getSpecification_VariableCharacterisation().getSpecification() + " to " + newRateStr);

		workParameter.getSpecification_VariableCharacterisation().setSpecification(newRateStr);
	}

}