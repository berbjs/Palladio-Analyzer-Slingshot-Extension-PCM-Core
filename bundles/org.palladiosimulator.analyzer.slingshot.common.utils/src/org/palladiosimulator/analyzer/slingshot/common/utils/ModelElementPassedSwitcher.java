package org.palladiosimulator.analyzer.slingshot.common.utils;

import java.util.Objects;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

public class ModelElementPassedSwitcher<T> extends Switch<T> {
	
	private final Switch<T> delegate;
	
	public ModelElementPassedSwitcher(final Switch<T> delegate) {
		this.delegate = Objects.requireNonNull(delegate);
	}

	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return true;
	}

	@Override
	public T doSwitch(EObject eObject) {
		// TODO Auto-generated method stub
		return super.doSwitch(eObject);
	}

}
