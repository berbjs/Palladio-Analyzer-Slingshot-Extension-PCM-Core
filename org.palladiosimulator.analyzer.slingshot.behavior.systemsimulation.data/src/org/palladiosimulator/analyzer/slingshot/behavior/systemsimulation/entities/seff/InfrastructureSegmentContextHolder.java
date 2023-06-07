package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.SeffBehaviorContextHolder;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.SeffBehaviorWrapper;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.seff_performance.InfrastructureCall;

import de.uka.ipd.sdq.simucomframework.variables.StackContext;

/**
 *
 * Tracks the progress during {@link InfrastructureCall} interpretation.
 *
 * Basically an iterator over all InfrastructureCalls of an InternalAction.
 *
 * @author Sarah Stieß
 *
 */
public class InfrastructureSegmentContextHolder extends SeffBehaviorContextHolder implements Iterator<InfrastructureCall> {

	private final Iterator<InfrastructureCall> calls;

	public InfrastructureSegmentContextHolder(final SEFFInterpretationContext enclosingSEFFContext,
			final InternalAction enclosingInternalAction) {
		super(List.of(), Optional.empty(), Optional.empty());

		final List<InfrastructureCall> tmp = new ArrayList<>();

		final EList<InfrastructureCall> infrastructureCalls = enclosingInternalAction.getInfrastructureCall__Action();

		for (final InfrastructureCall infrastructureCall : infrastructureCalls) {
			final int numberOfCalls = StackContext.evaluateStatic(
					infrastructureCall.getNumberOfCalls__InfrastructureCall().getSpecification(), Integer.class,
					enclosingSEFFContext.getRequestProcessingContext().getUser().getStack().currentStackFrame());

			for (int i = 0; i < numberOfCalls; i++) {
				tmp.add(infrastructureCall);
			}
		}

		this.calls = tmp.iterator();
	}

	@Override
	public boolean hasNext() {
		return this.calls.hasNext();
	}

	@Override
	public InfrastructureCall next() {
		return this.calls.next();
	}

	@Override
	public SeffBehaviorWrapper getCurrentProcessedBehavior() {
		throw new IllegalStateException("this is Infra Context");
	}

	@Override
	public boolean hasFinished() {
		return !this.hasNext();
	}

	@Override
	public AbstractAction getNextAction() {
		throw new IllegalStateException("this is Infra Context");
	}

	@Override
	public Optional<AbstractAction> getSuccessor() {
		throw new IllegalStateException("this is Infra Context");
	}
}
