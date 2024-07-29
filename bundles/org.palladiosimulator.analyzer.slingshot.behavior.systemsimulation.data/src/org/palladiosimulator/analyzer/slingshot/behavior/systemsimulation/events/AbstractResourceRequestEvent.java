package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractEntityChangedEvent;

/**
 * This abstract event class is an upper bound of events that contain information about a
 * resource request. Keep in mind that this module does not process resources. Instead,
 * events of this type might either describe a request that an active or passive resource
 * is needed/required, or describe the result of that request. The actual processing
 * of the resources is done by a Resource simulator.
 * 
 * @author Julijan Katic
 *
 */
public abstract class AbstractResourceRequestEvent extends AbstractEntityChangedEvent<ResourceDemandRequest> {

	public AbstractResourceRequestEvent(final ResourceDemandRequest entity, final double delay) {
		super(entity, delay);
	}

	public AbstractResourceRequestEvent(final ResourceDemandRequest entity) {
		this(entity, 0);
	}
}
