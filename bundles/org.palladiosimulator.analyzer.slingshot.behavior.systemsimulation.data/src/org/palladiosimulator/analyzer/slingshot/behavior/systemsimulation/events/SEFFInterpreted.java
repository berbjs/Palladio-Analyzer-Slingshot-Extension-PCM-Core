package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;

/**
 * This interface serves the purpose of having an upper bound for all SEFF specific
 * events with regards to the type system. The SEFF interpreter should use this
 * interface to indicate that only events of this type will be published. Hence,
 * every event described in this package that is used by the SEFF interpreter
 * should be a sub-type of this interface.
 * <p>
 * Besides this, this interface does not define further functionalities or requirements.
 * 
 * @author Julijan Katic
 * 
 */
public interface SEFFInterpreted extends DESEvent {

}
