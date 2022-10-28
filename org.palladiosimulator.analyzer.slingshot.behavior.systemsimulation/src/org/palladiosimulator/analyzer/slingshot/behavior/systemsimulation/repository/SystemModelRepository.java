package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.repository;

import java.util.Optional;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.repository.Role;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.palladiosimulator.pcm.system.System;

import com.google.inject.ImplementedBy;

/**
 * A repository for system models that handles further handlers for the
 * {@code System} model. All methods that return an {@link Optional} do never
 * return {@code null}, but an empty Optional instead.
 * <p>
 * The repository allows a direct access to the model instead of using a
 * Switch-class.
 * 
 * @author Julijan Katic
 */
@ImplementedBy(SystemModelRepositoryImpl.class)
public interface SystemModelRepository {

	/**
	 * Loads the system model into the repository. This method should be called
	 * first, otherwise all other methods will not work.
	 * 
	 * @param system the non-null system model to load.
	 */
	void load(System system);

	/**
	 * Finds the service effect specification from a required role and a signature.
	 * This is useful if a SEFF calls another SEFF from a Required Role.
	 * 
	 * @param requiredRole The required role in which the SEFF lies.
	 * @param signature    The specific method to call.
	 * @return The SEFF of the method from that required role if it exists.
	 */
	Optional<ServiceEffectSpecification> findSeffFromRequiredRole(RequiredRole requiredRole, Signature signature);

	/**
	 * Returns the Assembly Context of a required role. The required role is
	 * attached to a specific assembly context, which in turn contains the component
	 * containg the SEFFs.
	 * 
	 * @param requiredRole The required role (connector) attached to the assembly
	 *                     context which should be found.
	 * @return The assembly context if it exists.
	 */
	Optional<AssemblyContext> findAssemblyContextFromRequiredRole(RequiredRole requiredRole);

	/**
	 * Get the delegation connector from a provided role. The provided role is the
	 * role attached to the whole system, not the role attached to an (inner)
	 * assembly context.
	 * 
	 * @param providedRole The provided role of a system.
	 * @return The delegation connector of that provided role.
	 */
	Optional<ProvidedDelegationConnector> getConnectedProvidedDelegationConnector(ProvidedRole providedRole);

	/**
	 * Returns the SEFF of a delegation connector at the certain signature. This is
	 * useful if someone ones to enter the system and call a service from the
	 * outside. This method requires to know the delegation already. If only the
	 * provided role (of the system!) is known, use {@link #getSeffFromProvidedRole}
	 * 
	 * @param connector The delegation connector connecting the (outer) provided
	 *                  role from the system to the inner provided role to a certain
	 *                  assembly context.
	 * @param signature The signature at which the SEFF lies.
	 * @return The SEFF if it exists.
	 */
	Optional<ServiceEffectSpecification> getDelegatedComponentSeff(ProvidedDelegationConnector connector,
			Signature signature);

	/**
	 * Returns the SEFF from a provided role at the signature. In this case, the
	 * provided role could be either a provided role of the overall system that is
	 * delegated to an inner service, or it already points to a concrete provided
	 * role of an assembly context.
	 * 
	 * @param role      The provided role in which the service should be found.
	 * @param signature the signature at which the SEFF lies.
	 * @return The SEFF if it exists.
	 */
	Optional<ServiceEffectSpecification> getSeffFromProvidedRole(ProvidedRole role, Signature signature);

	/**
	 * Returns the Assembly Context onto which the provided role is attached.
	 * 
	 * @param role the provided role.
	 * @return the assembly context.
	 */
	Optional<AssemblyContext> findAssemblyContextByProvidedRole(ProvidedRole role);

	/**
	 * A short-cut method to find the assembly context by any role, regardless of
	 * whether it is a required or a provided role. However, the role must be either
	 * one of them.
	 * 
	 * @param role The role attached to an assembly context.
	 * @return The attached assembly context.
	 * @see #findAssemblyContextByProvidedRole(ProvidedRole)
	 * @see #findAssemblyContextFromRequiredRole(RequiredRole)
	 */
	Optional<AssemblyContext> findAssemblyContextByRole(Role role);

	/**
	 * Returns the default instance of this interface.
	 * 
	 * @return an instance implementing this interface.
	 */
	static SystemModelRepository getDefaultInstance() {
		return INSTANCE;
	}

	/**
	 * A default instance implementing this interface.
	 */
	SystemModelRepository INSTANCE = new SystemModelRepositoryImpl();

}
