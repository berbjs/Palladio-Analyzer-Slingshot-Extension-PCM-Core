package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.CallOverWireRequest;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;

public abstract class AbstractCallOverWireEvent extends AbstractSimulationEvent {

	private final CallOverWireRequest callOverWireRequest;

	public AbstractCallOverWireEvent(final CallOverWireRequest callOverWireRequest) {
		this.callOverWireRequest = callOverWireRequest;
	}

	public CallOverWireRequest getRequest() {
		return this.callOverWireRequest;
	}

}
