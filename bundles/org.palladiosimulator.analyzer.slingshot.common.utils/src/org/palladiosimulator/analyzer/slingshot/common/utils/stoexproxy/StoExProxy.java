package org.palladiosimulator.analyzer.slingshot.common.utils.stoexproxy;

import org.palladiosimulator.pcm.core.PCMRandomVariable;

/**
 * An interface describing a proxy element to calculate
 * the StoEx on each access.
 * 
 * @author Julijan Katic
 *
 * @param <T>
 */
public interface StoExProxy<T> {
	
	/**
	 * Returns the instance of the random variable
	 * that is specified in this proxy.
	 * 
	 * @return the instance of a PCM random variable.
	 */
	PCMRandomVariable getRandomVariable();
	
	/**
	 * Returns the instance of the evaluated
	 * random variable.
	 * 
	 * @return instance of the evaluated RV.
	 */
	T calculateRV();
}
