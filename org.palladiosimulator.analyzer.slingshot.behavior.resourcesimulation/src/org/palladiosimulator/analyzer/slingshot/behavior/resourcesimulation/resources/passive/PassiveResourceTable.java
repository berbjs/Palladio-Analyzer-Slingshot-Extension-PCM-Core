package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.passive;

import java.util.Optional;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.IPassiveResource;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.IPassiveResourceTable;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.AbstractResourceTable;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.PassiveResource;

import de.uka.ipd.sdq.simucomframework.variables.StackContext;

/**
 * The table that contains all the available passive resources. The table has to
 * be built first by calling {@link #buildTable(Allocation)}.
 * 
 * @author Julijan Katic
 *
 */
public final class PassiveResourceTable
		extends AbstractResourceTable<PassiveResourceCompoundKey, SimplePassiveResource>
		implements IPassiveResourceTable {

	/**
	 * Builds the table with a given {@link Allocation}. The table will then be
	 * filled with different {@link SimplePassiveResource}s mapped by its
	 * {@link PassiveResourceCompoundKey}.
	 * 
	 * @param allocation The model containing all the passive resource information.
	 */
	public void buildTable(final Allocation allocation) {
		allocation.getAllocationContexts_Allocation().stream()
				.map(AllocationContext::getAssemblyContext_AllocationContext)
				.filter(allocationContext -> allocationContext
						.getEncapsulatedComponent__AssemblyContext() instanceof BasicComponent)
				.forEach(this::createPassiveResources);
	}

	/**
	 * Creates passive resources from the {@link AssemblyContext}. This can only be
	 * done if the encapsulated component is an instance of {@link BasicComponent}
	 * which holds a list of passive resources.
	 * 
	 * @param assemblyContext the assembly whose encapsulated basic component holds
	 *                        a list of passive resources.
	 */
	private void createPassiveResources(final AssemblyContext assemblyContext) {
		assert assemblyContext.getEncapsulatedComponent__AssemblyContext() instanceof BasicComponent;

		final BasicComponent component = (BasicComponent) assemblyContext
				.getEncapsulatedComponent__AssemblyContext();

		component.getPassiveResource_BasicComponent().forEach(passiveResource -> {
			final long capacity = StackContext.evaluateStatic(
					passiveResource.getCapacity_PassiveResource().getSpecification(), Long.class);
			final PassiveResourceCompoundKey id = new PassiveResourceCompoundKey(passiveResource,
					assemblyContext);
			this.resources.put(id, new SimplePassiveResource(id, capacity));
		});
	}

	/**
	 * Checks whether the table is currently empty or not.
	 * 
	 * @return true if empty.
	 */
	@Override
	public boolean isEmpty() {
		return this.resources.isEmpty();
	}

	/**
	 * Returns a {@link SimplePassiveResource} mapped by its id. If it is not
	 * present, an empty optional will be returned.
	 * 
	 * @param id The id of the passive resource to be returned.
	 * @return The simple passive resource if present.
	 */
	public Optional<SimplePassiveResource> getPassiveResource(final PassiveResourceCompoundKey id) {
		if (!this.resources.containsKey(id)) {
			return Optional.empty();
		}

		return Optional.of(this.resources.get(id));
	}

	@Override
	public Optional<? extends IPassiveResource> getPassiveResourceFromModelElement(
			final PassiveResource passiveResource, final AssemblyContext assemblyContext) {
		final PassiveResourceCompoundKey key = PassiveResourceCompoundKey.of(passiveResource, assemblyContext);
		return this.getPassiveResource(key);
	}
}
