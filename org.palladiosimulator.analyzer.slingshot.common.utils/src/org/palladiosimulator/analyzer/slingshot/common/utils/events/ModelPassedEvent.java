package org.palladiosimulator.analyzer.slingshot.common.utils.events;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractGenericEvent;

public class ModelPassedEvent<M extends EObject> extends AbstractGenericEvent<M, M> {
	
	@SuppressWarnings("unchecked")
	public ModelPassedEvent(final M modelElement) {
		super((Class<M>) modelElement.getClass(), modelElement, 0.0);
	}
	
	public M getModelElement() {
		return this.getEntity();
	}
}
