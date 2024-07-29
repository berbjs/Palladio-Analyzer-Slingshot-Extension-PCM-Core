package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources;

import java.util.Optional;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.PassiveResource;

public interface IPassiveResourceTable {

	public Optional<? extends IPassiveResource> getPassiveResourceFromModelElement(
			final PassiveResource passiveResource, final AssemblyContext assemblyContext);

	public boolean isEmpty();

}
