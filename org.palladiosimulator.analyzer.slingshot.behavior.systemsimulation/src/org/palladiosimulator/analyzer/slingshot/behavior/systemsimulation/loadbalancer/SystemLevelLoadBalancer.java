package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.loadbalancer;

import java.util.Optional;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;

/**
 * This interface specifies the abilities a load balancer needs. This is
 * especially useful when multiple implementations will come in the future.
 * 
 * @author Julijan Katic
 *
 */
public interface SystemLevelLoadBalancer {

	/**
	 * Returns the assembly context on which the user should take the path. In
	 * theory, one component can be in multiple assembly contexts, and that again
	 * will be allocated to some resource environment.
	 * 
	 * Thus, the load balancing happens here already, since this will also balance
	 * out the resource containers.
	 * 
	 * @param providedRole The role in which the user wants to call a service.
	 * @return An assembly context which the load balancer has chosen according to
	 *         some distribution.
	 */
	public Optional<AssemblyContext> getAssemblyContext(final OperationProvidedRole providedRole);

}
