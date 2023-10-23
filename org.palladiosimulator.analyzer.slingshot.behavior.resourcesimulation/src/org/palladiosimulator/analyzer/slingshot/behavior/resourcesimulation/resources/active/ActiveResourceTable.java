package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active;

import java.util.Optional;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.ProcessingRate;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.AbstractResourceTable;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;
import org.palladiosimulator.pcm.resourcetype.SchedulingPolicy;

import de.uka.ipd.sdq.simucomframework.resources.SchedulingStrategy;

/**
 * The resource table maps every provided resource to a specific resource
 * instance.
 *
 * @author Julijan Katic
 */
public final class ActiveResourceTable extends AbstractResourceTable<ActiveResourceCompoundKey, ActiveResource> {

	/**
	 * Creates a new {@link ActiveResource} accordingly to the {@code spec}.
	 *
	 * @param spec the specification of the resource.
	 */
	public void createNewResource(final ResourceContainer container, final ProcessingResourceSpecification spec) {
		final int numberOfReplicas = spec.getNumberOfReplicas();
		final SchedulingPolicy schedulingPolicy = spec.getSchedulingPolicy();
		
		final ActiveResourceCompoundKey id = this.getKey(container, spec);

		final ActiveResource resource;
		final String resourceName;

		switch (SchedulingPolicyId.retrieveFromSchedulingPolicy(schedulingPolicy)) {
		case FCFS:
			resourceName = SchedulingStrategy.FCFS.toString();
			resource = new FCFSResource(id, resourceName, numberOfReplicas, new ProcessingRate(spec.getProcessingRate_ProcessingResourceSpecification()));
			break;

		case PROCESSOR_SHARING:
			resourceName = SchedulingStrategy.PROCESSOR_SHARING.toString();
			resource = new ProcessorSharingResource(id, resourceName, numberOfReplicas, new ProcessingRate(spec.getProcessingRate_ProcessingResourceSpecification()));
			break;

//		case DELAY:
//			resourceName = SchedulingStrategy.DELAY.toString();
//			resource = new DelayResource(resourceId, resourceName);
//			break;

		default:
			/* In case of a resource type that is not known, nothing should happen
			 * --> Other extensions might handle this already.
			 */
			resource = null;
			resourceName = "";
			break;
		}

		if (resource != null) {
			this.resources.put(id, resource);
		}
	}


	public void buildModel(final Allocation allocation) {
		allocation.getAllocationContexts_Allocation().stream()
				.map(AllocationContext::getResourceContainer_AllocationContext)
				.forEach(this::createActiveResourcesFromResourceContainer);
	}

	public void createActiveResourcesFromResourceContainer(final ResourceContainer resourceContainer) {
		resourceContainer.getActiveResourceSpecifications_ResourceContainer()
				.forEach(spec -> this.createNewResource(resourceContainer, spec));
	}
	
	public void removeActiveResources(final ResourceContainer resourceContainer) {
		resourceContainer.getActiveResourceSpecifications_ResourceContainer().stream()
						 .map(spec -> this.getKey(resourceContainer, spec))
						 .filter(id -> this.resources.containsKey(id))
						 .forEach(id -> this.resources.remove(id));
	}
	
	public ActiveResourceCompoundKey getKey(final ResourceContainer container, final ProcessingResourceSpecification spec) {
		final ProcessingResourceType activeResourceType = spec.getActiveResourceType_ActiveResourceSpecification();
		
		return new ActiveResourceCompoundKey(container, activeResourceType);
	}

	public Optional<ActiveResource> getActiveResource(final ActiveResourceCompoundKey id) {
		if (!this.resources.containsKey(id)) {
			return Optional.empty();
		}

		return Optional.of(this.resources.get(id));
	}

}
