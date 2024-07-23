package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities;

import org.palladiosimulator.analyzer.slingshot.common.utils.stoexproxy.AbstractStoExProxy;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

/**
 * This is a proxy class that re-evaluates the think time for closed workload
 * users each time the getter method is called.
 * 
 * @author Julijan Katic
 */
public final class ThinkTime extends AbstractStoExProxy<Double> {

	public ThinkTime(final PCMRandomVariable thinkTimeRV) {
		super(thinkTimeRV, Double.class);
	}

}
