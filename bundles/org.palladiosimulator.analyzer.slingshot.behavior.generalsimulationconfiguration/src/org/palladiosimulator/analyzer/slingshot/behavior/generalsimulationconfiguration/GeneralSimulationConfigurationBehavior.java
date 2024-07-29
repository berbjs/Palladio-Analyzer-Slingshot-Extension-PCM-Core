package org.palladiosimulator.analyzer.slingshot.behavior.generalsimulationconfiguration;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;

import de.uka.ipd.sdq.probfunction.math.IProbabilityFunctionFactory;
import de.uka.ipd.sdq.probfunction.math.impl.ProbabilityFunctionFactoryImpl;
import de.uka.ipd.sdq.simucomframework.variables.cache.StoExCache;

/**
 * This behavior is used in order to have a general implementation for configuraring
 * the simulator beforehand. For instance, this class initializes the {@link StoEx} cache
 * on the simulation start.
 * 
 * @author Julijan Katic
 * @version 1.0
 */
public class GeneralSimulationConfigurationBehavior implements SimulationBehaviorExtension {
	
	private final static Logger LOGGER = Logger.getLogger(GeneralSimulationConfigurationBehavior.class);
	
	@Inject
	public GeneralSimulationConfigurationBehavior() {
		this.initializeStoExCache();
	}
	
	/**
	 * Initializes the ProbFunction and StoExCache.
	 */
	private void initializeStoExCache() {
		final IProbabilityFunctionFactory probabilityFunctionFactory = ProbabilityFunctionFactoryImpl.getInstance();
		StoExCache.initialiseStoExCache(probabilityFunctionFactory);
		LOGGER.info("Initialized probability function");
	}
}
