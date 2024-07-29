package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior.BranchScenarioContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior.LoopScenarioBehaviorContext;

/**
 * This event indicates that the user is entering a inner scenario behavior. A
 * inner scenario behavior is typically the scenario specified within a loop or
 * branch.
 * <p>
 * In order for this event to work properly, the
 * {@link UserInterpretationContext#getParentContext()} should point to the
 * outer context, and also
 * {@link UserInterpretationContext#getBehaviorContext()} should be an instance
 * of {@link LoopScenarioBehaviorContext} if the scenario belongs to a Loop, or
 * {@link BranchScenarioContext} if the scenario belongs to a branch.
 * <p>
 * If the parent contexts are not set, it won't be possible to track the root
 * scenario of the usage simulation.
 * 
 * @author Julijan Katic
 *
 */
public class InnerScenarioBehaviorInitiated extends AbstractUserChangedEvent {

	public InnerScenarioBehaviorInitiated(final UserInterpretationContext entity, final double delay) {
		super(entity, delay);
	}

}
