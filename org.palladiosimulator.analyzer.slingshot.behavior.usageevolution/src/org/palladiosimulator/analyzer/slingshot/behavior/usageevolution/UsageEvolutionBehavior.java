package org.palladiosimulator.analyzer.slingshot.behavior.usageevolution;

import static org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality.MANY;
import static org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality.SINGLE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.usageevolution.events.IntervalPassed;
import org.palladiosimulator.analyzer.slingshot.behavior.usageevolution.evolver.AbstractUsageEvolver;
import org.palladiosimulator.analyzer.slingshot.behavior.usageevolution.evolver.LoopingUsageEvolver;
import org.palladiosimulator.analyzer.slingshot.behavior.usageevolution.evolver.StretchedUsageEvolver;
import org.palladiosimulator.analyzer.slingshot.common.annotations.Nullable;
import org.palladiosimulator.analyzer.slingshot.core.events.PreSimulationConfigurationStarted;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.scaledl.usageevolution.Usage;
import org.scaledl.usageevolution.UsageEvolution;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;

/**
 * The System simulation behavior is a extension that simulates the system
 * model. It listens to events requesting to interpret the repository and
 * sometimes will result in a SEFF Interpretation request if there is a RDSeff.
 *
 * @author Julijan Katic
 */
@OnEvent(when = IntervalPassed.class, then = IntervalPassed.class, cardinality = SINGLE)
@OnEvent(when = PreSimulationConfigurationStarted.class, then = IntervalPassed.class, cardinality = MANY)
public class UsageEvolutionBehavior implements SimulationBehaviorExtension {

	private static final Logger LOGGER = Logger.getLogger(UsageEvolutionBehavior.class);


	private final UsageEvolution usageEvolutionModel;

	private final Map<Usage, AbstractUsageEvolver> usage2evolver;

	private final Optional<Double> maxDuration;

	@Inject
	public UsageEvolutionBehavior(@Nullable final UsageEvolution usageEvolutionModel, final SimuComConfig config) {
		this.usageEvolutionModel = usageEvolutionModel;
		this.usage2evolver = new HashMap<>();

		maxDuration = config.getSimuTime() > 0 ? Optional.of((double) config.getSimuTime()) : Optional.empty();
	}

	@Override
	public boolean isActive() {
		return usageEvolutionModel != null;
	}

	@Subscribe
	public Result<IntervalPassed> onPreSimulationConfigurationStarted(
			final PreSimulationConfigurationStarted preSimulationConfigurationStarted) {

		final Set<IntervalPassed> events = new HashSet<>();

		for (final Usage usage : this.usageEvolutionModel.getUsages()) {
			AbstractUsageEvolver evolver;
			if (usage.isRepeatingPattern()) {
				evolver = new LoopingUsageEvolver(usage);
			} else {
				evolver = new StretchedUsageEvolver(usage, maxDuration);
				// SimuLizar also stretched the update interval, but i think thats moot
			}

			usage2evolver.put(usage, evolver);

			// schedule at 0 to evolve load right away.
			events.add(new IntervalPassed(0, usage));
		}

		return Result.of(events);
	}

	@Subscribe
	public Result<?> onIntervalPassed(final IntervalPassed intervalPassed) {

		final Usage usage = intervalPassed.getUsage();

		final AbstractUsageEvolver evolver = usage2evolver.get(usage);
		evolver.triggerInternal(intervalPassed.time());

		return Result.of(new IntervalPassed(usage.getEvolutionStepWidth(), usage));
	}

}
