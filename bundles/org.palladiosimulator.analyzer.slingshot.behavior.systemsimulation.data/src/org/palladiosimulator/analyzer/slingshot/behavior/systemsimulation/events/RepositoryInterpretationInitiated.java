package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.RepositoryInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEntityChangedEvent;

/**
 * Event that is used to check whether the a repository should be interpreted
 * for a user. This is typically the case when the User wants to entry the
 * system, or when the SEFF tries make an external call.
 * 
 * @author Julijan Katic
 */
public final class RepositoryInterpretationInitiated extends AbstractEntityChangedEvent<RepositoryInterpretationContext> {

	public RepositoryInterpretationInitiated(final RepositoryInterpretationContext entity, final double delay) {
		super(entity, delay);
	}

}
