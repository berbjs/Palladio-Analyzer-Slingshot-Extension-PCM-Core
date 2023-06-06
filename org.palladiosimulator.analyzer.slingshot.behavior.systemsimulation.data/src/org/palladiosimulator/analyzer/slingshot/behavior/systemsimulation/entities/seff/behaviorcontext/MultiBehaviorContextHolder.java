package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;

import com.google.common.base.Preconditions;

public abstract class MultiBehaviorContextHolder extends SeffBehaviorContextHolder {

	protected MultiBehaviorContextHolder(final List<ResourceDemandingBehaviour> behaviors,
			final Optional<AbstractAction> successor, final Optional<SeffBehaviorWrapper> parent) {
		super(behaviors, successor, parent);
		Preconditions.checkArgument(behaviors.size() > 0);
	}

}
