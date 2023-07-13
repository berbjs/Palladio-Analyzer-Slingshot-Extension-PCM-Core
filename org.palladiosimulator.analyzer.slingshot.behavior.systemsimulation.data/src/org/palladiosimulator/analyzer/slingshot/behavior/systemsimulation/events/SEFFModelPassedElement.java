package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.common.utils.events.ModelPassedEvent;

public class SEFFModelPassedElement<M extends EObject> extends ModelPassedEvent<M> implements SEFFInterpreted {

	private final SEFFInterpretationContext context;

	public SEFFModelPassedElement(final M modelElement, final SEFFInterpretationContext context) {
		super(modelElement);
		this.context = context;
	}

	public SEFFInterpretationContext getContext() {
		return this.context;
	}

}
