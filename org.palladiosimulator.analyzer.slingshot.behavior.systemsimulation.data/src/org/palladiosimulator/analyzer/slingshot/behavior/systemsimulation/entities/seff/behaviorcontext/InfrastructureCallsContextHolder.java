package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.seff_performance.InfrastructureCall;

import de.uka.ipd.sdq.simucomframework.variables.StackContext;

/**
 *
 * Tracks the progress during {@link InfrastructureCall} interpretation.
 *
 * Basically an iterator over all InfrastructureCalls of an InternalAction.
 *
 * Does not hold a SEFFBehaviourContext, but the Context of InfratructureCalls
 * of an InternalAction, and those Calls are a simple List without counters.
 *
 *
 * @author Sarah Stieß
 *
 */
public class InfrastructureCallsContextHolder extends SeffBehaviorContextHolder {

	private final Iterator<InfrastructureCall> calls;

	public InfrastructureCallsContextHolder(final SEFFInterpretationContext enclosingSEFFContext,
			final InternalAction enclosingInternalAction, final SeffBehaviorWrapper parent) {
		super(List.of(), Optional.empty(), Optional.of(parent));

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
	public SeffBehaviorWrapper getCurrentProcessedBehavior() {
		throw new IllegalStateException(
				"InfrastructureSegmentContextHolder has no current processed behaviour, as InfraCalls are modelled as a simple list.");
	}

	@Override
	public boolean hasFinished() {
		return !this.calls.hasNext();
	}

	@Override
	public InfrastructureCall getNextAction() {
		return this.calls.next();
	}
}
