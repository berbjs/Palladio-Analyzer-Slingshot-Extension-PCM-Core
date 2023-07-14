package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.monitor;

import java.util.Objects;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.ProvidedRole;

/**
 *
 * Compound Key of AssemblyContext and ProvidedRole, as only the combination of
 * both uniquely identifies a provided role (defined on component level) of a
 * specific assembly context.
 *
 * @author Sarah Stieß
 *
 */
public final class AssemblyOperationCompoundKey {

	private final AssemblyContext assemblyContext;
	private final ProvidedRole providedRole;

	private AssemblyOperationCompoundKey(final AssemblyContext resourceContainer, final ProvidedRole providedRole) {
		this.assemblyContext = resourceContainer;
		this.providedRole = providedRole;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.assemblyContext.getId(), this.providedRole.getId());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AssemblyOperationCompoundKey)) {
			return false;
		}
		final AssemblyOperationCompoundKey other = (AssemblyOperationCompoundKey) obj;
		return Objects.equals(this.assemblyContext.getId(), other.assemblyContext.getId())
				&& Objects.equals(this.providedRole.getId(), other.providedRole.getId());
	}

	@Override
	public String toString() {
		return this.assemblyContext.getId() + ":" + this.providedRole.getId();
	}

	public static AssemblyOperationCompoundKey of(final AssemblyContext resourceContainer,
			final ProvidedRole providedRole) {
		return new AssemblyOperationCompoundKey(resourceContainer, providedRole);
	}
}
