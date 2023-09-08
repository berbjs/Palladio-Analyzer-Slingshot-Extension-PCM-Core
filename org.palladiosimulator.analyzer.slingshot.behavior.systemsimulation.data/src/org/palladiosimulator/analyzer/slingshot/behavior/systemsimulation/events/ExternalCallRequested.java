package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.Signature;

/**
 * This event is used to indicate that a call from a SEFF to somewhere outside
 * is being made (i.e., the call to another component of the system). This is
 * important insofar that the other component *might* be on another resource
 * container, and thus the latency of the network must be incorporated.
 * 
 * @author Julijan Katic
 */
public final class ExternalCallRequested extends AbstractSimulationEvent {

	private final AssemblyContext from;
	private final AssemblyContext to;
	private final Signature signature;
	private final User user;

	public ExternalCallRequested(final AssemblyContext from, final AssemblyContext to,
			final Signature signature, final User user) {
		super();
		this.from = from;
		this.to = to;
		this.signature = signature;
		this.user = user;
	}

	public AssemblyContext getFrom() {
		return from;
	}

	public AssemblyContext getTo() {
		return to;
	}

	public Signature getSignature() {
		return signature;
	}

	public User getUser() {
		return user;
	}
}
