package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources;

import org.palladiosimulator.analyzer.slingshot.common.utils.stoexproxy.AbstractStoExProxy;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

/**
 * Proxy class responsible to calculate the processing for an active resource.
 * 
 * @author klinakuf
 */
public class ProcessingRate extends AbstractStoExProxy<Double>  {

	public ProcessingRate(PCMRandomVariable randomVariable) {
		super(randomVariable, Double.class);
		// TODO Auto-generated constructor stub
	}

}
