package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.common.utils.events.ModelPassedEvent;

public final class UsageModelPassedElement<M extends EObject> extends ModelPassedEvent<M> {

	private final UserInterpretationContext context;

	public UsageModelPassedElement(final M modelElement, final UserInterpretationContext context) {
		super(modelElement);
		this.context = context;
	}

	public UserInterpretationContext getContext() {
		return this.context;
	}

}
