package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.repository;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.repository.Role;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.palladiosimulator.pcm.system.System;

/**
 * A default implementation of the {@link SystemModelRepository}. Instead of
 * directly instantiating this class, use either
 * {@link SystemModelRepository#getDefaultInstance()} or use the {@code @Inject}
 * annotation and let the module system inject this instance.
 * 
 * @author Julijan Katic
 */
public class SystemModelRepositoryImpl implements SystemModelRepository {

	private static final Logger LOGGER = Logger.getLogger(SystemModelRepositoryImpl.class);

	private System systemModel;

	@Override
	public void load(final System system) {
		this.systemModel = system;
	}

	@Override
	public Optional<ServiceEffectSpecification> findSeffFromRequiredRole(final RequiredRole requiredRole,
			final Signature signature) {
		return this.systemModel.getConnectors__ComposedStructure().stream()
				.filter(connector -> connector instanceof AssemblyConnector)
				.map(connector -> (AssemblyConnector) connector)
				.filter(assemblyConnector -> assemblyConnector.getRequiredRole_AssemblyConnector().getId()
						.equals(requiredRole.getId()))
				.map(AssemblyConnector::getProvidingAssemblyContext_AssemblyConnector)
				.map(AssemblyContext::getEncapsulatedComponent__AssemblyContext)
				.filter(BasicComponent.class::isInstance).map(BasicComponent.class::cast)
				.map(component -> this.getSeffFromBasicComponent(component, signature)).filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();

	}

	public Optional<AssemblyContext> findAssemblyContextFromRepositoryComponent(final RepositoryComponent component) {
		return this.systemModel.getAssemblyContexts__ComposedStructure().stream()
				.filter(context -> context.getEncapsulatedComponent__AssemblyContext().getId()
						.equals(component.getId()))
				.findFirst();
	}

	@Override
	public Optional<AssemblyContext> findAssemblyContextFromRequiredRole(final RequiredRole requiredRole) {
		return this.systemModel.getConnectors__ComposedStructure().stream()
				.filter(connector -> connector instanceof AssemblyConnector)
				.map(connector -> (AssemblyConnector) connector)
				.filter(assemblyConnector -> assemblyConnector.getRequiredRole_AssemblyConnector().getId()
						.equals(requiredRole.getId()))
				.map(AssemblyConnector::getProvidingAssemblyContext_AssemblyConnector)
				.findFirst();
	}

	@Override
	public Optional<ProvidedDelegationConnector> getConnectedProvidedDelegationConnector(
			final ProvidedRole providedRole) {

		final boolean providedRolePresent = this.systemModel.getProvidedRoles_InterfaceProvidingEntity().stream()
				.filter(systemProvidedRole -> systemProvidedRole.getId().equals(providedRole.getId()))
				.findFirst()
				.isPresent();

		if (!providedRolePresent) {
			return Optional.empty();
		}

		LOGGER.debug("Provided Role is present: " + providedRole.getEntityName());

		return this.systemModel.getConnectors__ComposedStructure().stream()
				.filter(ProvidedDelegationConnector.class::isInstance)
				.map(ProvidedDelegationConnector.class::cast)
				.filter(connector -> connector.getOuterProvidedRole_ProvidedDelegationConnector().getId()
						.equals(providedRole.getId()))
				.findFirst();
	}

	@Override
	public Optional<ServiceEffectSpecification> getDelegatedComponentSeff(final ProvidedDelegationConnector connector,
			final Signature signature) {
		final ProvidedRole role = connector.getInnerProvidedRole_ProvidedDelegationConnector();
		return this.getSeffFromProvidedRole(role, signature);
	}

	@Override
	public Optional<ServiceEffectSpecification> getSeffFromProvidedRole(final ProvidedRole role,
			final Signature signature) {

		LOGGER.info("Find SEFF: " + role.getEntityName() + " (ProvidedRole) and " + signature.getEntityName()
				+ " (Signature)");

		LOGGER.debug(
				"Number of assembly contexts: " + this.systemModel.getAssemblyContexts__ComposedStructure().size());

		return this.systemModel.getAssemblyContexts__ComposedStructure().stream()
				.map(AssemblyContext::getEncapsulatedComponent__AssemblyContext)
				.peek(component -> LOGGER.debug("Encapsulated Component: " + component.getEntityName()))
				.filter(component -> component.getProvidedRoles_InterfaceProvidingEntity().stream()
						.anyMatch(providedRole -> providedRole.getId().equals(role.getId())))
				.peek(component -> LOGGER.info("Found the component: " + component.getEntityName()))
				.filter(BasicComponent.class::isInstance)
				.map(BasicComponent.class::cast)
				.peek(component -> LOGGER.info("Found the basic component: " + component.getEntityName()))
				.map(basicComponent -> this.getSeffFromBasicComponent(basicComponent, signature))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();

	}

	@Override
	public Optional<AssemblyContext> findAssemblyContextByProvidedRole(final ProvidedRole role) {
		LOGGER.debug("findAssemblyContextByProvidedRole: ProvidedRole[id=" + role.getId() + "]");
//		return this.systemModel.getConnectors__ComposedStructure().stream()
//				.filter(AssemblyConnector.class::isInstance)
//				.map(AssemblyConnector.class::cast)
//				.peek(connector -> LOGGER
//						.debug("AssemblyConnector found: " + connector.getProvidedRole_AssemblyConnector().getId()))
//				.filter(connector -> connector.getProvidedRole_AssemblyConnector().getId().equals(role.getId()))
//				.map(AssemblyConnector::getProvidingAssemblyContext_AssemblyConnector)
//				.findFirst();
		return this.systemModel.getAssemblyContexts__ComposedStructure().stream()
				.filter(context -> this.isProvidedRoleInComponent(context.getEncapsulatedComponent__AssemblyContext(),
						role))
				.findFirst();
	}

	private boolean isProvidedRoleInComponent(final RepositoryComponent component, final ProvidedRole role) {
		return component.getProvidedRoles_InterfaceProvidingEntity().stream()
				.anyMatch(providedRole -> providedRole.getId().equals(role.getId()));
	}

	@Override
	public Optional<AssemblyContext> findAssemblyContextByRole(final Role role) {
		final Optional<AssemblyContext> result;
		if (role instanceof ProvidedRole) {
			result = this.findAssemblyContextByProvidedRole((ProvidedRole) role);
		} else if (role instanceof RequiredRole) {
			result = this.findAssemblyContextFromRequiredRole((RequiredRole) role);
		} else {
			throw new IllegalArgumentException("The role must be either a ProvidedRole or a RequiredRole.");
		}
		return result;
	}

	public Optional<ServiceEffectSpecification> getSeffFromBasicComponent(final BasicComponent basicComponent,
			final Signature signature) {

		LOGGER.info("Start searching for seff: " + basicComponent.getEntityName());
		return basicComponent.getServiceEffectSpecifications__BasicComponent().stream()
				.filter(spec -> spec.getDescribedService__SEFF().getId().equals(signature.getId()))
				.findFirst();

	}

}
