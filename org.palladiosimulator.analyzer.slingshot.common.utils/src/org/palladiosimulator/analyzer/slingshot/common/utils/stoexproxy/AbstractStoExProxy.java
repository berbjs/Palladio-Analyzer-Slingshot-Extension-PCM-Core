package org.palladiosimulator.analyzer.slingshot.common.utils.stoexproxy;

import org.palladiosimulator.pcm.core.PCMRandomVariable;

import de.uka.ipd.sdq.simucomframework.variables.StackContext;

/**
 * An abstract class defining the basic calculation proxy.
 * 
 * @author Julijan Katic
 *
 * @param <T> type to which the random variable should be evaluated.
 */
public abstract class AbstractStoExProxy<T> implements StoExProxy<T> {
	
	private final PCMRandomVariable randomVariable;
	private final Class<T> evaluateInto;
	
	public AbstractStoExProxy(final PCMRandomVariable randomVariable, final Class<T> evaluateInto) {
		this.randomVariable = randomVariable;
		this.evaluateInto = evaluateInto;
	}
	
	@Override
	public PCMRandomVariable getRandomVariable() {
		return randomVariable;
	}

	@Override
	public T calculateRV() {
		return StackContext.evaluateStatic(randomVariable.getSpecification(), evaluateInto);
	}

}
