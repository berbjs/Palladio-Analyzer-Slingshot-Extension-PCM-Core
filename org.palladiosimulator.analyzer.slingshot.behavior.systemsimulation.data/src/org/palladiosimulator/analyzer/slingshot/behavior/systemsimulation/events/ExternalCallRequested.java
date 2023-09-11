package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.CallOverWireRequest;

/**
 * This event is used to indicate that a call from a SEFF to somewhere outside
 * is being made (i.e., the call to another component of the system). This is
 * important insofar that the other component *might* be on another resource
 * container, and thus the latency of the network must be incorporated.
 * 
 * @author Julijan Katic
 */
public final class ExternalCallRequested extends AbstractCallOverWireEvent {

	public ExternalCallRequested(final CallOverWireRequest callOverWireRequest) {
		super(callOverWireRequest);
	}


}
