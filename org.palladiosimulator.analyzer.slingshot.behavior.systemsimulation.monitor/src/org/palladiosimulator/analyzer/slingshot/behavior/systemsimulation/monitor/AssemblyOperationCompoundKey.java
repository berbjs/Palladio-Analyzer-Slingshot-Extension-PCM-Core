package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.monitor;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Signature;

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
	private final Signature signature;

	private AssemblyOperationCompoundKey(final AssemblyContext resourceContainer, final ProvidedRole providedRole,
			final Signature signature) {
		this.assemblyContext = resourceContainer;
		this.providedRole = providedRole;
		this.signature = signature;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.assemblyContext.getId(), this.providedRole.getId(), this.signature.getId());
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
				&& Objects.equals(this.providedRole.getId(), other.providedRole.getId())
				&& Objects.equals(this.signature.getId(), other.signature.getId());
	}

	@Override
	public String toString() {
		return this.assemblyContext.getId() + ":" + this.providedRole.getId();
	}

	public static AssemblyOperationCompoundKey of(final SEFFInterpretationContext context) {

		Signature sig = null;
		if (context.getRequestProcessingContext().getUserRequest() != null) {
			sig = context.getRequestProcessingContext().getUserRequest().getOperationSignature();
		} else if (context.getCallOverWireRequest().isPresent()) {
			sig = context.getCallOverWireRequest().get().getSignature();
		} else {
			throw new IllegalArgumentException(String.format(
					"Cannot determine Signature of SEFFInterpretationContext %s, as their is neither a UserRequest, nor a CallOverWireRequest.",
					context.toString()));
		}

		return new AssemblyOperationCompoundKey(context.getAssemblyContext(),
				context.getRequestProcessingContext().getProvidedRole(), sig);
	}

	public static AssemblyOperationCompoundKey of(final AssemblyContext resourceContainer,
			final ProvidedRole providedRole, final Signature signature) {
		return new AssemblyOperationCompoundKey(resourceContainer, providedRole, signature);
	}
}
