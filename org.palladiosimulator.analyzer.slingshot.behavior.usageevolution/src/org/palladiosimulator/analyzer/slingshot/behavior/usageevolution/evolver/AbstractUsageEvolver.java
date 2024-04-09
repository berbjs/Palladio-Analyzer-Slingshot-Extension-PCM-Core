package org.palladiosimulator.analyzer.slingshot.behavior.usageevolution.evolver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.parameter.VariableCharacterisation;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.Workload;
import org.scaledl.usageevolution.Usage;
import org.scaledl.usageevolution.WorkParameterEvolution;

import tools.descartes.dlim.Sequence;
import tools.descartes.dlim.generator.ModelEvaluator;

/**
 * Usage evolver which updates the workload according to a Usage Evolution
 * model.
 *
 * @author Sarah Stieß, based on {@code PeriodicallyTriggeredUsageEvolver}
 *
 */
public abstract class AbstractUsageEvolver {

	static final Logger LOGGER = Logger.getLogger(AbstractUsageEvolver.class);

	protected final UsageScenario evolvedScenario;

	private final Map<Usage, ModelEvaluator> cachedLoadEvaluators = new HashMap<Usage, ModelEvaluator>();

	private final Map<Usage, Map<VariableCharacterisation, ModelEvaluator>> cachedWorkEvaluators = new HashMap<Usage, Map<VariableCharacterisation, ModelEvaluator>>();

	private final Usage usage;


	/**
	 * Constructs the looping usage evolver.
	 *
	 * @param rtState         SimuLizar runtime state.
	 * @param firstOccurrence First point in time at which the evolver should evolve
	 *                        the load.
	 * @param delay           The interval in which the evolver should evolve the
	 *                        load. -- TODO not needed?
	 * @param evolvedScenario The evolved scenario.
	 */
	public AbstractUsageEvolver(final UsageScenario evolvedScenario, final Usage usage) {
		this.usage = usage;
		this.evolvedScenario = evolvedScenario;
	}

	/**
	 * Get the load evaluator for <code>this</code>.
	 *
	 * @return The load evaluator.
	 */
	protected ModelEvaluator getLoadEvaluator() {
		final Usage usage = this.getCorrespondingUsage();
		ModelEvaluator evaluator = this.cachedLoadEvaluators.get(usage);
		if (evaluator == null) {
			final Sequence loadEvolutionSequence = usage.getLoadEvolution();
			if (loadEvolutionSequence != null) {
				evaluator = new ModelEvaluator(loadEvolutionSequence);
				this.cachedLoadEvaluators.put(usage, evaluator);
			}
		}
		return evaluator;
	}

	/**
	 * Get the Usage updated by <code>this</code>.
	 *
	 * @return the Usage updated by <code>this</code>.
	 */
	protected Usage getCorrespondingUsage() {
		return this.usage;
	}

	/**
	 * Gets all the Work Evaluators for the Work Parameter evolutions of
	 * <code>this</code>.
	 *
	 * @return The Work Evaluators for the Work Parameter evolutions of
	 *         <code>this</code>.
	 */
	protected Map<VariableCharacterisation, ModelEvaluator> getWorkEvaluators() {
		// TODO: consider optimizing this by changing workEvaluators to be an instance
		// variable that
		// is lazily initialized by this call. Must first determine whether this has
		// side
		// effects on the simulation.

		// Build the hashmap from work parameters of the first usage in the usage
		// evolution
		// and to their corresponding model evaluators
		final Usage usage = this.getCorrespondingUsage();
		Map<VariableCharacterisation, ModelEvaluator> workEvaluators = this.cachedWorkEvaluators.get(usage);
		if (workEvaluators == null) {
			if (usage != null && usage.getWorkEvolutions() != null && usage.getWorkEvolutions().size() > 0) {
				workEvaluators = new HashMap<VariableCharacterisation, ModelEvaluator>();
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
				this.cachedWorkEvaluators.put(usage, workEvaluators);
			} else {
				workEvaluators = Collections.emptyMap();
			}
		}

		return workEvaluators;
	}

	/**
	 *
	 * Actually evolves the load.
	 *
	 *
	 */
	public void triggerInternal(final double time) {

		// First, evolve load if load evaluator exists
		final ModelEvaluator loadEvaluator = this.getLoadEvaluator();

		if (loadEvaluator != null) {
			this.evolveLoad(loadEvaluator, time);
		}

		// Then, iterate through work parameters to evolve
		final Map<VariableCharacterisation, ModelEvaluator> workEvaluators = this.getWorkEvaluators();
		for (final VariableCharacterisation workParam : workEvaluators.keySet()) {
			this.evolveWork(workParam, workEvaluators.get(workParam), time);
		}
	}

	/**
	 * Asssumption : in SimuLizar this was necessary, because each user got their
	 * private copy of the PCM models. Thus, it's not needed anymore with Slingshot.
	 *
	 * @param workParam
	 * @return
	 */
//	private VariableCharacterisation getGlobalWorkParameter(final VariableCharacterisation workParam) {
//		final VariableCharacterisation globalWorkParam = (VariableCharacterisation) pcmPartition.getResourceSet()
//				.getEObject(EcoreUtil.getURI(workParam), false);
//		return globalWorkParam;
//	}

	/**
	 * The length of <code>this</code>' DLIM sequence.
	 *
	 * @return The length of <code>this</code>' DLIM sequence.
	 */
	protected double getDLIMFinalDuration() {
		return this.getCorrespondingUsage().getLoadEvolution().getFinalDuration();
	}

	/**
	 * Evolve the load.
	 *
	 * @param loadEvaluator DLIM evaluator used to fetch the load at the current
	 *                      point in time.
	 */
	protected void evolveLoad(final ModelEvaluator loadEvaluator, final double time) {

		double newRate = this.getNewRate(loadEvaluator, time);
		final Workload wl = this.getCorrespondingUsage().getScenario().getWorkload_UsageScenario();
		if (wl != null) {
			if (wl instanceof OpenWorkload) {
				final PCMRandomVariable openwl = ((OpenWorkload) wl).getInterArrivalTime_OpenWorkload();
				if (newRate != 0) {
					// Using inverse value to convert from arrival rate to inter arrival
					// time
					newRate = 1 / newRate;
				} else {
					// If the arrival rate is 0, the inter arrival time should be
					// infinite. We use the max integer value as an approximation
					newRate = Integer.MAX_VALUE;
				}

				final String newRateStr = Double.toString(newRate);
				if (newRateStr.equals(openwl.getSpecification())) {
					LOGGER.warn("Inter arrival time is still: " + newRateStr);
				} else {
					LOGGER.warn(
							"Changing inter arrival time from: " + openwl.getSpecification() + " to :" + newRateStr);
					openwl.setSpecification(newRateStr);
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