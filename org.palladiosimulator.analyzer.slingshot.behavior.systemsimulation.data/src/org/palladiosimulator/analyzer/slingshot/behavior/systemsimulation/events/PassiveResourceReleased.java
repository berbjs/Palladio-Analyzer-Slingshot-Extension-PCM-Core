package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;

/**
 * Notifies the resource simulator that a passive resource has been released by a certain SEFF.
 * This can cause another SEFF that has requested this (passive) resource to acquire it directly.
 * 
 * @author Julijan Katic
 */
public final class PassiveResourceReleased extends AbstractResourceRequestEvent implements SEFFInterpreted {

	public PassiveResourceReleased(final ResourceDemandRequest entity, final double delay) {
		super(entity, delay);
	}

	public PassiveResourceReleased(final ResourceDemandRequest entity) {
		this(entity, 0);
	}
}
