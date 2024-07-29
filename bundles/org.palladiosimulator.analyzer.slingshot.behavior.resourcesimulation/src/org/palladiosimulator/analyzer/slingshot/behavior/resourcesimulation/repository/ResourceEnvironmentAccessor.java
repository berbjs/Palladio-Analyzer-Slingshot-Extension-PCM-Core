package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.repository;

import java.util.Optional;

import javax.inject.Inject;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.resourcetype.ResourceType;

/**
 * The resource environment accessor tries to find information about the
 * resource environment. It provides operations to find the correct resource
 * container for a certain component.
 * 
 * @author Julijan Katic
 */
public class ResourceEnvironmentAccessor {

	private final ResourceEnvironment resourceEnvironment;
	private final Allocation allocation;

	@Inject
	public ResourceEnvironmentAccessor(final Allocation allocation) {
		this.allocation = allocation;
		this.resourceEnvironment = allocation.getTargetResourceEnvironment_Allocation();
	}

	public Optional<ProcessingResourceSpecification> findResourceSpecification(final ResourceContainer container,
			final ResourceType type) {
		assert this.resourceEnvironment.getResourceContainer_ResourceEnvironment().contains(container);

		return container.getActiveResourceSpecifications_ResourceContainer().stream()
				.filter(spec -> type.getId().equals(spec.getActiveResourceType_ActiveResourceSpecification().getId()))
				.findFirst();
	}

	public Optional<AllocationContext> findResourceContainerOfComponent(final AssemblyContext assemblyContext) {
		return this.allocation.getAllocationContexts_Allocation().stream()
				.filter(allocationContext -> allocationContext.getAssemblyContext_AllocationContext().getId()
						.equals(assemblyContext.getId()))
				.findFirst();
	}

	public static ResourceEnvironmentAccessor with(final Allocation allocation) {
		return new ResourceEnvironmentAccessor(allocation);
	}
}
