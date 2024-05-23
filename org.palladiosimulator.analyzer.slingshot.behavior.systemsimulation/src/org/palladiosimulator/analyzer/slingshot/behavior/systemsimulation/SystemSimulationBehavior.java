package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation;

import static org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality.MANY;
import static org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality.SINGLE;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.GeneralEntryRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.RepositoryInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.CallOverWireRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.InfrastructureCallsContextHolder;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.ActiveResourceFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.CallOverWireAborted;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.CallOverWireRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.CallOverWireSucceeded;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.RepositoryInterpretationInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.ResourceDemandRequestAborted;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFExternalActionCalled;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInfrastructureCalled;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInterpretationProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.interpreters.RepositoryInterpreter;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.repository.SystemModelRepository;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserAborted;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserEntryRequested;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.common.utils.SimulatedStackHelper;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;

import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStackframe;

/**
 * The System simulation behavior is a extension that simulates the system
 * model. It listens to events requesting to interpret the repository and
 * sometimes will result in a SEFF Interpretation request if there is a RDSeff.
 *
 * @author Julijan Katic, Floriment Klinaku
 */
@OnEvent(when = UserEntryRequested.class, then = SEFFInterpretationProgressed.class, cardinality = SINGLE)
@OnEvent(when = RepositoryInterpretationInitiated.class, then = SEFFInterpretationProgressed.class, cardinality = MANY)
@OnEvent(when = SEFFExternalActionCalled.class, then = CallOverWireRequested.class, cardinality = MANY)
@OnEvent(when = CallOverWireSucceeded.class, then = {SEFFInterpretationProgressed.class, UserAborted.class}, cardinality = MANY)
@OnEvent(when = CallOverWireAborted.class, then = CallOverWireRequested.class, cardinality = MANY)
@OnEvent(when = ActiveResourceFinished.class, then = SEFFInterpretationProgressed.class, cardinality = MANY)
@OnEvent(when = SEFFInfrastructureCalled.class, then = SEFFInterpretationProgressed.class, cardinality = SINGLE)
@OnEvent(when = ResourceDemandRequestAborted.class, then = UserAborted.class, cardinality = SINGLE)
public class SystemSimulationBehavior implements SimulationBehaviorExtension {

	private static final Logger LOGGER = Logger.getLogger(SystemSimulationBehavior.class);

	private final Allocation allocationModel;
	private final SystemModelRepository systemRepository;

	@Inject
	public SystemSimulationBehavior(final Allocation allocationModel, final SystemModelRepository repository) {
		this.allocationModel = allocationModel;
		this.systemRepository = repository;
		this.init();
	}

	public void init() {
		this.systemRepository.load(this.allocationModel.getSystem_Allocation());
	}

	/**
	 * Used to interpret the entry request from a usage model.
	 */
	@Subscribe
	public Result<SEFFInterpretationProgressed> onUserEntryRequested(final UserEntryRequested userEntryRequested) {
		final UserRequest request = userEntryRequested.getEntity();

		final OperationProvidedRole operationProvidedRole = request.getOperationProvidedRole();
		final OperationSignature operationSignature = request.getOperationSignature();
		final EList<VariableUsage> variableUsages = request.getVariableUsages();

		/* Receive the assembly context and its seff. */
		final Optional<ProvidedDelegationConnector> connectedProvidedDelegationConnector = this.systemRepository
				.getConnectedProvidedDelegationConnector(operationProvidedRole);
		if (connectedProvidedDelegationConnector.isEmpty()) {
			LOGGER.info("This is not an entry request! Role" + operationProvidedRole.getEntityName());
			return Result.of();
		}

		final Optional<ServiceEffectSpecification> seffFromProvidedRole = this.systemRepository
				.getDelegatedComponentSeff(connectedProvidedDelegationConnector.get(), operationSignature);
		final Optional<AssemblyContext> assemblyContextByProvidedRole = this.systemRepository
				.findAssemblyContextByProvidedRole(
						connectedProvidedDelegationConnector.get().getInnerProvidedRole_ProvidedDelegationConnector());

		LOGGER.debug("SEFF? " + seffFromProvidedRole.isPresent() + " | AssemblyContext? "
				+ assemblyContextByProvidedRole.isPresent());

		if (seffFromProvidedRole.isPresent() && assemblyContextByProvidedRole.isPresent()) {
			SimulatedStackHelper.createAndPushNewStackFrame(request.getUser().getStack(), variableUsages);
			// further stack frames are pushed inside the RepositoryInterpreter.


			final ServiceEffectSpecification seff = seffFromProvidedRole.get();

			assert seff instanceof ResourceDemandingBehaviour;

			final RepositoryInterpreter interpreter = new RepositoryInterpreter(assemblyContextByProvidedRole.get(),
					operationSignature, operationProvidedRole, request.getUser(), systemRepository, Optional.empty(),
					null, new SimulatedStackframe<Object>(), userEntryRequested.getUserInterpretationContext(),
					request);

			final Set<SEFFInterpretationProgressed> res = interpreter
					.doSwitch(assemblyContextByProvidedRole.get().getEncapsulatedComponent__AssemblyContext());

			return Result.of(res);

		}
		LOGGER.info("Either seff or assembly context is not found => stop interpretation for this request.");

		return Result.of();
	}

	/**
	 * This event will handle the repository interpretation of a system, especially
	 * for the entry of a system.
	 *
	 * TODO: Is this needed?
	 */
	@Subscribe
	public Result<SEFFInterpretationProgressed> onRepositoryInterpretationInitiated(
			final RepositoryInterpretationInitiated event) {
		final RepositoryInterpretationContext context = event.getEntity();

		final RepositoryInterpreter interpreter = new RepositoryInterpreter(context.getAssemblyContext(),
				context.getSignature(), context.getProvidedRole(), context.getUser(), this.systemRepository,
				Optional.empty(), null, null);
		final Set<SEFFInterpretationProgressed> appearedEvents = interpreter.doSwitch(context.getProvidedRole());

		return Result.of(appearedEvents);
	}

	/**
	 * Used to interpret the next SEFF that is requested by another seff. For
	 * example, when an External Call action was performed.
	 */
	@Subscribe
	public Result<?> onRequestInitiated(final SEFFExternalActionCalled requestInitiated) {

		return requestCallOverWire(requestInitiated.getEntity());
	}

	/**
	 * Helper method for creating an ExternalCallRequested with the right variable
	 * usage to consider
	 *
	 * @param entity
	 * @return
	 */
	private Result<?> requestCallOverWire(final GeneralEntryRequest entity) {
		final Optional<AssemblyContext> assemblyContext = this.systemRepository
				.findAssemblyContextFromRequiredRole(entity.getRequiredRole());

		final Optional<OperationProvidedRole> providedRole = this.systemRepository
				.findProvidedRoleFromRequiredRole(entity.getRequiredRole());

		if (assemblyContext.isPresent() && providedRole.isPresent()) {
			final SimulatedStackframe<Object> inputStackframe = SimulatedStackHelper
					.createAndPushNewStackFrame(entity.getUser().getStack(), entity.getInputVariableUsages());

			final CallOverWireRequest request = CallOverWireRequest.builder()
					.from(entity.getRequestFrom().getAssemblyContext()).to(assemblyContext.get())
					.signature(entity.getSignature()).user(entity.getUser()).entryRequest(entity)
					.variablesToConsider(inputStackframe).build();

			// TODO: Should we do the check whether resource containers are connected here?

			return Result.of(new CallOverWireRequested(request));
		}
		return Result.of();
	}

	/**
	 *
	 * If the call over wire was a success, continue with the interpretation of the
	 * SEFF.
	 *
	 * If either the {@code AssemblyContext} or {@code OperationProvidedRole} are
	 * missing, e.g. due to a scale while the call was processed in the linking
	 * resource, the Request cannot be completed. Thus this operation published a
	 * {@link UserAborted} event for the user of the request, such that the request
	 * finishes gracefully.
	 *
	 * This is especially important for closed workloads, where no new users enter
	 * the system, i.e. if the {@link UserAborted} is not published the simulation
	 * "looses" the user entirely.
	 *
	 * It is probably less important for open workloads, as new users keep entering
	 * the system.
	 *
	 *
	 * @param cowSucceeded
	 * @return
	 */
	@Subscribe
	public Result<?> onCallOverWireSucceeded(final CallOverWireSucceeded cowSucceeded) {
		final GeneralEntryRequest entity = cowSucceeded.getRequest().getEntryRequest();

		if (cowSucceeded.getRequest().getReplyTo().isPresent()) {
			/*
			 * This is a reply to an already made request from a caller, so we need to go
			 * back to the caller
			 */
			final SEFFInterpretationContext seffInterpretationContext = entity.getRequestFrom();

			/* Pop input variable Usages */
			entity.getUser().getStack().removeStackFrame();

			/* Push the output variables to the parent stack */
			SimulatedStackHelper.addParameterToStackFrame(cowSucceeded.getRequest().getVariablesToConsider(),
					entity.getOutputVariableUsages(), entity.getUser().getStack().currentStackFrame());

			return Result.of(new SEFFInterpretationProgressed(seffInterpretationContext));
		}

		final Optional<AssemblyContext> assemblyContext = this.systemRepository
				.findAssemblyContextFromRequiredRole(entity.getRequiredRole());

		final Optional<OperationProvidedRole> providedRole = this.systemRepository
				.findProvidedRoleFromRequiredRole(entity.getRequiredRole());

		if (assemblyContext.isPresent() && providedRole.isPresent()) {
			final RepositoryInterpreter interpreter = new RepositoryInterpreter(assemblyContext.get(),
					entity.getSignature(), providedRole.get(), entity.getUser(), this.systemRepository,
					Optional.of(entity.getRequestFrom()), cowSucceeded.getRequest(), new SimulatedStackframe<Object>());

			/* Interpret the Component of the system. */
			final Set<SEFFInterpretationProgressed> appearedEvents = interpreter
					.doSwitch(assemblyContext.get().getEncapsulatedComponent__AssemblyContext());

			return Result.of(appearedEvents);
		}
		LOGGER.debug(String.format("Could not continue after %s to %s. BEWARE : untested edge case!!!",
				CallOverWireSucceeded.class.getSimpleName(), entity.getRequiredRole().toString()));

		return Result.of(new UserAborted(cowSucceeded.getRequest().getEntryRequest().getRequestFrom()
				.getRequestProcessingContext().getUserInterpretationContext()));
	}

	@Subscribe
	public Result<?> onCallOverWireAborted(final CallOverWireAborted cowAborted) {
		LOGGER.info("The call over wire was aborted, retry");
		/* Pop input variable Usages */
		cowAborted.getRequest().getUser().getStack().removeStackFrame();

		return requestCallOverWire(cowAborted.getRequest().getEntryRequest());
	}

	/**
	 * The subscribe method handles the case when for any reason the resource demand
	 * requested could not be handled. The main use case is: resources/assemblies
	 * that previously existed, do not exist anymore, causing null-pointers when
	 * accessing state.
	 *
	 * @param demandRequestAborted
	 * @return UserAborted event.
	 */
	@Subscribe
	public Result<UserAborted> onResourceDemandRequestAborted(final ResourceDemandRequestAborted demandRequestAborted) {

		return Result.of(new UserAborted(
				findUserInterpretationContext(demandRequestAborted.getEntity().getSeffInterpretationContext())));

	}

	/**
	 * Helper method to traverse the SeffInterpretationContext and find the
	 * UserInterpretationContext.
	 *
	 * @param seffContext
	 * @return
	 * @throws NoSuchElementException
	 */
	private UserInterpretationContext findUserInterpretationContext(final SEFFInterpretationContext seffContext)
			throws NoSuchElementException {
		if (seffContext.getRequestProcessingContext().getUserInterpretationContext() != null) {
			return seffContext.getRequestProcessingContext().getUserInterpretationContext();
		}
		if (seffContext.getCaller().isPresent()) {
			return findUserInterpretationContext(seffContext.getCaller().get());
		} else if (seffContext.getParent().isPresent()) {
			return findUserInterpretationContext(seffContext.getParent().get());
		}
		throw new NoSuchElementException("User Interpretation Context not found!");
	}

	/**
	 * TODO it would be possible to scratch {@link SEFFInfrastructureCalled} and use
	 * {@link SEFFExternalActionCalled} for infrastructure calls as well. However,
	 * in my opinion it is better to keep them separated for now, as it is still
	 * unclear what will happen to the handling of external calls.
	 *
	 * @param infraCall
	 * @return
	 */
	@Subscribe
	public Result<SEFFInterpretationProgressed> onSEFFInfrastructureCalled(final SEFFInfrastructureCalled infraCall) {
		final GeneralEntryRequest entity = infraCall.getEntity();

		final Optional<AssemblyContext> assemblyContext = this.systemRepository
				.findInfrastructureAssemblyContextFromRequiredRole(entity.getRequiredRole());

		if (assemblyContext.isPresent()) {
			final RepositoryInterpreter interpreter = new RepositoryInterpreter(assemblyContext.get(),
					entity.getSignature(), null, entity.getUser(), this.systemRepository,
					Optional.of(entity.getRequestFrom()), null, null);

			/* Interpret the Component of the system. */
			final Set<SEFFInterpretationProgressed> appearedEvents = interpreter
					.doSwitch(assemblyContext.get().getEncapsulatedComponent__AssemblyContext());

			return Result.of(appearedEvents);
		}
		return Result.of();
	}

	@Subscribe
	public Result<DESEvent> onActiveResourceFinished(final ActiveResourceFinished activeResourceFinished) {

		final SEFFInterpretationContext parentContext = activeResourceFinished.getEntity()
				.getSeffInterpretationContext();
		final AbstractAction parentalAction = parentContext.getBehaviorContext().getCurrentProcessedBehavior()
				.getCurrentAction().getPredecessor_AbstractAction();

		// we are "done" with the internal action that called the active resource, i.e.
		// current action is already the "stop" action.

		// not an internal action or no infra calls? continue normally.
		if (!(parentalAction instanceof InternalAction)
				|| ((InternalAction) parentalAction).getInfrastructureCall__Action().isEmpty()) {
			return Result.of(new SEFFInterpretationProgressed(
					activeResourceFinished.getEntity().getSeffInterpretationContext()));
		}

		final InfrastructureCallsContextHolder infraContext = new InfrastructureCallsContextHolder(
				activeResourceFinished.getEntity().getSeffInterpretationContext(), (InternalAction) parentalAction,
				activeResourceFinished.getEntity().getSeffInterpretationContext().getBehaviorContext()
						.getCurrentProcessedBehavior());

		final SEFFInterpretationContext infraChildContext = SEFFInterpretationContext.builder()
				.withBehaviorContext(infraContext)
				.withRequestProcessingContext(parentContext.getRequestProcessingContext())
				.withCaller(parentContext.getCaller()).withAssemblyContext(parentContext.getAssemblyContext()).build();

		return Result.of(new SEFFInterpretationProgressed(infraChildContext));
	}

}
