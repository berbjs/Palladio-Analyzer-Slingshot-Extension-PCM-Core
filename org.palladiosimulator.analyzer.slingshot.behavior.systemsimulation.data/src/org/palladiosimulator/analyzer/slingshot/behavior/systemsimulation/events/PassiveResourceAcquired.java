package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;

/**
 * Notifies that the passive resource has been acquired by the SEFF and can
 * proceed calculating. The SEFF that is requesting a passive resource should
 * therefore wait for this event before continuing.
 * 
 * @author Julijan Katic
 *
 */
public final class PassiveResourceAcquired extends AbstractResourceRequestEvent implements SEFFInterpreted {

	public PassiveResourceAcquired(final ResourceDemandRequest entity, final double delay) {
		super(entity, delay);
	}

	public PassiveResourceAcquired(final ResourceDemandRequest entity) {
		super(entity);
	}

}
