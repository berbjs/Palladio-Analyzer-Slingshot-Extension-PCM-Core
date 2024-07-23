package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.AbstractResourceRequestEvent;

/**
 * Event to announce the state of a passive resource.
 *
 * @author Sarah Stieß
 *
 */
public final class PassiveResourceStateUpdated extends AbstractResourceRequestEvent {

	private final long freeTokens;

	public PassiveResourceStateUpdated(final ResourceDemandRequest entity, final long freeTokens) {
		super(entity, 0.0);
		this.freeTokens = freeTokens;
	}

	public long freeTokens() {
		return this.freeTokens;
	}
}
