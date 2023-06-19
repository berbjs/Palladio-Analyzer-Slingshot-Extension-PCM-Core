package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.interpreters;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.GeneralEntryRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest.ResourceType;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.InfrastructureSegmentContextHolder;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.BranchBehaviorContextHolder;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.ForkBehaviorContextHolder;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.LoopBehaviorContextHolder;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.PassiveResourceReleased;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.ResourceDemandRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFChildInterpretationStarted;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFExternalActionCalled;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInfrastructureCalled;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInfrastructureCallsProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInterpretationFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInterpretationProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInterpreted;
import org.palladiosimulator.analyzer.slingshot.common.utils.SimulatedStackHelper;
import org.palladiosimulator.analyzer.slingshot.common.utils.TransitionDeterminer;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Parameter;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.AcquireAction;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.CallAction;
import org.palladiosimulator.pcm.seff.CollectionIteratorAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ForkAction;
import org.palladiosimulator.pcm.seff.ForkedBehaviour;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.LoopAction;
import org.palladiosimulator.pcm.seff.ReleaseAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.SetVariableAction;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;
import org.palladiosimulator.pcm.seff.seff_performance.InfrastructureCall;
import org.palladiosimulator.pcm.seff.seff_performance.ParametricResourceDemand;
import org.palladiosimulator.pcm.seff.util.SeffSwitch;

import de.uka.ipd.sdq.simucomframework.variables.StackContext;
import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStack;
import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStackframe;

/**
 * The interpreter uses a certain {@code Switch} (like the Visitor Pattern) to
 * iterate through the elements in the Seff model and interpret certain model
 * elements.
 * <p>
 * The Seff is typically bound to an assembly context and a certain user that
 * has the call stack.
 * <p>
 * It generates new events and returns them on each visit.
 *
 * @author Julijan Katic
 */
public class SeffInterpreter extends SeffSwitch<Set<SEFFInterpreted>> {

	private static final Logger LOGGER = Logger.getLogger(SeffInterpreter.class);

	private final SEFFInterpretationContext context;

	/**
	 * Instantiates the SeffInterpreter with the needed information of user context
	 * and assembly context entity. These information are needed as the seff always
	 * works on a certain call stack.
	 *
	 * @param context The interpretation context onto which the Seff specification
	 *                is bound.
	 */
	public SeffInterpreter(final SEFFInterpretationContext context) {
		this.context = context;
	}

	/**
	 * When a StopAction occurs, then no further interpretation of this event is
	 * needed and thus the request has been successfully interpreted.
	 *
	 * @return Set with a single {@link RequestFinished} event.
	 */
	@Override
	public Set<SEFFInterpreted> caseStopAction(final StopAction object) {
		LOGGER.debug("Seff stopped.");
		return Set.of(new SEFFInterpretationFinished(this.context));
	}

	@Override
	public Set<SEFFInterpreted> caseBranchAction(final BranchAction branchAction) {
		final EList<AbstractBranchTransition> abstractBranchTransitions = branchAction.getBranches_Branch();

		if (abstractBranchTransitions.isEmpty()) {
			throw new IllegalStateException("Empty branch action is not allowed!");
		}

		final TransitionDeterminer transitionDeterminer = new TransitionDeterminer(
				this.context.getRequestProcessingContext().getUser().getStack().currentStackFrame());
		final AbstractBranchTransition branchTransition = transitionDeterminer
				.determineTransition(abstractBranchTransitions);

		if (branchTransition == null) {
			throw new IllegalStateException("No branch transition was active. This is not allowed.");
		}

		LOGGER.info("Branch chosen: " + branchTransition.getEntityName());

		final BranchBehaviorContextHolder holder = new BranchBehaviorContextHolder(
				branchTransition.getBranchBehaviour_BranchTransition(), branchAction.getSuccessor_AbstractAction(),
				this.context.getBehaviorContext().getCurrentProcessedBehavior());
		final SEFFInterpretationContext childContext = SEFFInterpretationContext.builder().withBehaviorContext(holder)
				.withRequestProcessingContext(this.context.getRequestProcessingContext())
				.withCaller(this.context.getCaller())
				.withAssemblyContext(this.context.getAssemblyContext()).build();

		final SEFFChildInterpretationStarted event = new SEFFChildInterpretationStarted(childContext);

		return Set.of(event);
	}

	/**
	 * Always returns {@link SeffInterpretationRequested} with the successor object
	 * be interpreted next.
	 *
	 * @return Set of single {@link SEFFInterpretationProgressed}.
	 */
	@Override
	public Set<SEFFInterpreted> caseStartAction(final StartAction object) {
		LOGGER.debug("Found starting action of SEFF");
		return Set.of(new SEFFInterpretationProgressed(this.context));
	}

	@Override
	public Set<SEFFInterpreted> caseLoopAction(final LoopAction object) {
		final int iterationCount = StackContext.evaluateStatic(object.getIterationCount_LoopAction().getSpecification(),
				Integer.class, this.context.getRequestProcessingContext().getUser().getStack().currentStackFrame());

		LOGGER.info("LoopAction: Count " + iterationCount);

		final LoopBehaviorContextHolder holder = new LoopBehaviorContextHolder(object.getBodyBehaviour_Loop(),
				object.getSuccessor_AbstractAction(), this.context.getBehaviorContext().getCurrentProcessedBehavior(),
				iterationCount);
		final SEFFInterpretationContext childContext = SEFFInterpretationContext.builder().withBehaviorContext(holder)
				.withRequestProcessingContext(this.context.getRequestProcessingContext())
				.withCaller(this.context.getCaller())
				.withAssemblyContext(this.context.getAssemblyContext()).build();

		return Set.of(new SEFFChildInterpretationStarted(childContext));
	}

	@Override
	public Set<SEFFInterpreted> caseForkAction(final ForkAction object) {

		if (object.getSynchronisingBehaviours_ForkAction() == null) {
			throw new IllegalStateException("ForkAction must have a synchronisation point!");
		}

		final EList<ForkedBehaviour> forkedBehaviors = object.getSynchronisingBehaviours_ForkAction()
				.getSynchronousForkedBehaviours_SynchronisationPoint();

		final List<ResourceDemandingBehaviour> rdBehaviors = forkedBehaviors.stream()
				.map(b -> (ResourceDemandingBehaviour) b).collect(Collectors.toList());

		if (forkedBehaviors.isEmpty()) {
			throw new IllegalStateException("Empty forked behaviors is not allowed");
		}

		final ForkBehaviorContextHolder forkedBehaviorContext = new ForkBehaviorContextHolder(rdBehaviors,
				object.getSuccessor_AbstractAction(), this.context.getBehaviorContext().getCurrentProcessedBehavior());

		final List<SEFFInterpretationContext> childContexts = rdBehaviors.stream()
				.map(rdBehavior -> SEFFInterpretationContext.builder().withBehaviorContext(forkedBehaviorContext)
						.withRequestProcessingContext(this.context.getRequestProcessingContext())
						.withCaller(this.context.getCaller())
						.withAssemblyContext(this.context.getAssemblyContext()).build())
				.collect(Collectors.toList());

		return childContexts.stream().map(childContext -> new SEFFChildInterpretationStarted(childContext))
				.collect(Collectors.toSet());
	}

	/**
	 * An external call action requires to find the next SEFF specification onto
	 * which the spec is called; hence, this method will return a
	 * {@link SEFFExternalActionCalled} event to request a new searching and
	 * interpretation of the SEFF.
	 *
	 * @return Set with a single element {@link SEFFExternalActionCalled}.
	 */
	@Override
	public Set<SEFFInterpreted> caseExternalCallAction(final ExternalCallAction externalCall) {
		final OperationRequiredRole requiredRole = externalCall.getRole_ExternalService();
		final OperationSignature calledServiceSignature = externalCall.getCalledService_ExternalService();
		final EList<VariableUsage> inputVariableUsages = externalCall.getInputVariableUsages__CallAction();

		final GeneralEntryRequest entryRequest = GeneralEntryRequest.builder()
				.withInputVariableUsages(inputVariableUsages).withRequiredRole(requiredRole)
				.withSignature(calledServiceSignature).withUser(this.context.getRequestProcessingContext().getUser())
				.withRequestFrom(this.context.update().withCaller(this.context).build()).build();

		return Set.of(new SEFFExternalActionCalled(entryRequest));
	}

	@Override
	public Set<SEFFInterpreted> caseAcquireAction(final AcquireAction object) {

		final ParametricResourceDemand demand = object.getResourceDemand_Action().stream().findFirst().orElseThrow(
				() -> new NoSuchElementException("No parametric resource demand specified for AcquireAction!"));

		final ResourceDemandRequest request = ResourceDemandRequest.builder()
				.withAssemblyContext(this.context.getAssemblyContext())
				.withPassiveResource(object.getPassiveresource_AcquireAction()).withResourceType(ResourceType.PASSIVE)
				.withSeffInterpretationContext(this.context).withParametricResourceDemand(demand).build();

		return Set.of(new ResourceDemandRequested(request));
	}

	@Override
	public Set<SEFFInterpreted> caseReleaseAction(final ReleaseAction object) {

		final ParametricResourceDemand demand = object.getResourceDemand_Action().stream().findFirst().orElseThrow(
				() -> new NoSuchElementException("No parametric resource demand specified for ReleaseAction!"));

		final ResourceDemandRequest request = ResourceDemandRequest.builder().withResourceType(ResourceType.PASSIVE)
				.withSeffInterpretationContext(this.context).withAssemblyContext(this.context.getAssemblyContext())
				.withPassiveResource(object.getPassiveResource_ReleaseAction()).withParametricResourceDemand(demand)
				.build();

		return Set.of(new PassiveResourceReleased(request, 0), new SEFFInterpretationProgressed(context));
	}

	@Override
	public Set<SEFFInterpreted> caseCollectionIteratorAction(final CollectionIteratorAction object) {
		final Parameter parameter = object.getParameter_CollectionIteratorAction();

		// TODO: Why the following?
		final String idNumberOfLoops = parameter.getParameterName() + ".NUMBER_OF_ELEMENTS";
		final int iterationCount = StackContext.evaluateStatic(idNumberOfLoops, Integer.class,
				this.context.getRequestProcessingContext().getUser().getStack().currentStackFrame());

		/*
		 * Create new stack frame for value characterisations of inner collection
		 * variables.
		 */
		final SimulatedStackframe<Object> innerVariableStackFrame = this.context.getRequestProcessingContext().getUser()
				.getStack().createAndPushNewStackFrame(
						this.context.getRequestProcessingContext().getUser().getStack().currentStackFrame());

		/*
		 * Evaluate value characterization of inner collection variable, store them on
		 * created top most stack frame. Add a "." at the end of the parameter name
		 * because otherwise if we search for paramter name "ab" we also get variables
		 * called "abc".
		 */
		this.context.getRequestProcessingContext().getUser().evaluateInner(innerVariableStackFrame,
				parameter.getParameterName() + ".");

		final LoopBehaviorContextHolder holder = new LoopBehaviorContextHolder(object.getBodyBehaviour_Loop(),
				object.getSuccessor_AbstractAction(), this.context.getBehaviorContext().getCurrentProcessedBehavior(),
				iterationCount);
		final SEFFInterpretationContext newContext = this.context.update().withBehaviorContext(holder).build();

		return Set.of(new SEFFChildInterpretationStarted(newContext));
	}

	@Override
	public Set<SEFFInterpreted> caseSetVariableAction(final SetVariableAction object) {
		final SimulatedStack<Object> stack = this.context.getRequestProcessingContext().getUser().getStack();
		final SimulatedStackframe<Object> stackFrame = stack.currentStackFrame();
		SimulatedStackHelper.addParameterToStackFrame(stackFrame, object.getLocalVariableUsages_SetVariableAction(),
				stackFrame);
		return Set.of(new SEFFInterpretationProgressed(this.context));
	}
	
	@Override
	public Set<SEFFInterpreted> caseCallAction(final CallAction callAction){
		
		if(callAction instanceof InfrastructureCall) {
			InfrastructureCall call = (InfrastructureCall) callAction;
			// create infra call event.
		 	final GeneralEntryRequest request = GeneralEntryRequest.builder()
					.withInputVariableUsages(call.getInputVariableUsages__CallAction())
					.withRequiredRole(call.getRequiredRole__InfrastructureCall())
					.withSignature(call.getSignature__InfrastructureCall())
					.withUser(this.context.getRequestProcessingContext()
							.getUser())
					.withRequestFrom(
							this.context.update().withCaller(this.context)
									.build())
					.build();

			return Set.of(new SEFFInfrastructureCalled(request));
		}
		else {
			return Set.of();
		}
	}

	/**
	 * An internal action demands certain resources and hence, a
	 * {@link ResourceDemandRequested} will be returned for each demand specified.
	 */
	@Override
	public Set<SEFFInterpreted> caseInternalAction(final InternalAction internalAction) {
		LOGGER.debug("Found internal action");
		final Set<SEFFInterpreted> events = new HashSet<>();

		internalAction.getResourceDemand_Action().forEach(demand -> {
			LOGGER.debug("Demand found with: " + demand);

			final ResourceDemandRequest request = ResourceDemandRequest.builder()
					.withAssemblyContext(this.context.getAssemblyContext())
					.withSeffInterpretationContext(this.context)
					.withResourceType(ResourceType.ACTIVE).withParametricResourceDemand(demand).build();

			final ResourceDemandRequested requestEvent = new ResourceDemandRequested(request);
			events.add(requestEvent);
		});

		if (events.isEmpty()) { // no RD! go straight to Infra calls
			if (!internalAction.getInfrastructureCall__Action().isEmpty()) {
				final InfrastructureSegmentContextHolder infraContext = new InfrastructureSegmentContextHolder(
						this.context, internalAction, this.context.getBehaviorContext().getCurrentProcessedBehavior());

				final SEFFInterpretationContext infraChildContext = SEFFInterpretationContext.builder()
						.withBehaviorContext(infraContext)
						.withRequestProcessingContext(this.context.getRequestProcessingContext())
						.withCaller(this.context).withAssemblyContext(this.context.getAssemblyContext())
						.build();

				events.add(new SEFFInfrastructureCallsProgressed(infraChildContext));
			}
		}

		if (events.isEmpty()) { // empty internal action, just progress.
			events.add(new SEFFInterpretationProgressed(this.context));

		}

		return Collections.unmodifiableSet(events);
	}

	@Override
	public Set<SEFFInterpreted> doSwitch(final EClass eClass, final EObject eObject) {
		Set<SEFFInterpreted> result = super.doSwitch(eClass, eObject);
		if (result == null) {
			result = Set.of();
		}
		return result;
	}

}
