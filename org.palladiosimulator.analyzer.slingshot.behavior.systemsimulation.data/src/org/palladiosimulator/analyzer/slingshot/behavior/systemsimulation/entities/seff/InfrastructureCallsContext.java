package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.seff_performance.InfrastructureCall;

import de.uka.ipd.sdq.simucomframework.variables.StackContext;

/**
 *
 * TODO
 *
 * @author Sarah Stieß
 *
 */
public class InfrastructureCallsContext implements Iterator<InfrastructureCall> {

	private final SEFFInterpretationContext enclosingSEFF;
	private final InternalAction enclosingInternalAction;
	private final Iterator<InfrastructureCall> calls;

	public InfrastructureCallsContext(final SEFFInterpretationContext enclosingSEFF,
			final InternalAction enclosingInternalAction) {
		super();
		this.enclosingSEFF = enclosingSEFF;
		this.enclosingInternalAction = enclosingInternalAction;
		final List<InfrastructureCall> tmp = new ArrayList<>();

		final EList<InfrastructureCall> infrastructureCalls = enclosingInternalAction.getInfrastructureCall__Action();

		for (final InfrastructureCall infrastructureCall : infrastructureCalls) {
			final int numberOfCalls = StackContext.evaluateStatic(
					infrastructureCall.getNumberOfCalls__InfrastructureCall().getSpecification(), Integer.class,
					enclosingSEFF.getRequestProcessingContext().getUser().getStack().currentStackFrame());

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

	public SEFFInterpretationContext getEnclosingSEFF() {
		return enclosingSEFF;
	}

	public InternalAction getEnclosingInternalAction() {
		return enclosingInternalAction;
	}

}
