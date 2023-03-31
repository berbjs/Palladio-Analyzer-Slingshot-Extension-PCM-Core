package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation;

import static org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality.MANY;
import static org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality.SINGLE;

import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.GeneralEntryRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.RepositoryInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.RootBehaviorContextHolder;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.user.RequestProcessingContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.ActiveResourceFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.RepositoryInterpretationInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFExternalActionCalled;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInterpretationProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.interpreters.RepositoryInterpreter;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.loadbalancer.EquallyDistributedSystemLevelLoadBalancer;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.loadbalancer.SystemLevelLoadBalancer;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.repository.SystemModelRepository;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserEntryRequested;
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
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;

/**
 * The System simulation behavior is a extension that simulates the system
 * model. It listens to events requesting to interpret the repository and
 * sometimes will result in a SEFF Interpretation request if there is a RDSeff.
 *
 * @author Julijan Katic
 */
@OnEvent(when = UserEntryRequested.class, then = SEFFInterpretationProgressed.class, cardinality = SINGLE)
@OnEvent(when = RepositoryInterpretationInitiated.class, then = SEFFInterpretationProgressed.class, cardinality = MANY)
@OnEvent(when = SEFFExternalActionCalled.class, then = SEFFInterpretationProgressed.class, cardinality = MANY)
@OnEvent(when = ActiveResourceFinished.class, then = SEFFInterpretationProgressed.class, cardinality = MANY)
public class SystemSimulationBehavior implements SimulationBehaviorExtension {

	private static final Logger LOGGER = Logger.getLogger(SystemSimulationBehavior.class);

	private final Allocation allocationModel;
	private final SystemModelRepository systemRepository;
	private final SystemLevelLoadBalancer loadBalancer;

	@Inject
	public SystemSimulationBehavior(final Allocation allocationModel, final SystemModelRepository repository) {
		this.allocationModel = allocationModel;
		this.systemRepository = repository;
		this.loadBalancer = new EquallyDistributedSystemLevelLoadBalancer(allocationModel);
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
//		final Optional<AssemblyContext> assemblyContextByProvidedRole = this.systemRepository
//				.findAssemblyContextByProvidedRole(
//						connectedProvidedDelegationConnector.get().getInnerProvidedRole_ProvidedDelegationConnector());
		final Optional<AssemblyContext> assemblyContextByProvidedRole = this.loadBalancer.getAssemblyContext(
				connectedProvidedDelegationConnector.get().getInnerProvidedRole_ProvidedDelegationConnector());

		LOGGER.debug("SEFF? " + seffFromProvidedRole.isPresent() + " | AssemblyContext? "
				+ assemblyContextByProvidedRole.isPresent());

		if (seffFromProvidedRole.isPresent() && assemblyContextByProvidedRole.isPresent()) {
			SimulatedStackHelper.createAndPushNewStackFrame(request.getUser().getStack(), variableUsages);
			final ServiceEffectSpecification seff = seffFromProvidedRole.get();

			assert seff instanceof ResourceDemandingBehaviour;

			final RequestProcessingContext requestProcessingContext = RequestProcessingContext.builder()
					.withUser(request.getUser())
					.withUserRequest(request)
					.withUserInterpretationContext(userEntryRequested.getUserInterpretationContext())
					.withProvidedRole(operationProvidedRole)
					.withAssemblyContext(assemblyContextByProvidedRole.get())
					.build();

			final SEFFInterpretationContext context = SEFFInterpretationContext.builder()
					.withRequestProcessingContext(requestProcessingContext)
					.withAssemblyContext(assemblyContextByProvidedRole.get())
					.withBehaviorContext(new RootBehaviorContextHolder((ResourceDemandingBehaviour) seff))
					.build();

			return Result.of(new SEFFInterpretationProgressed(context));
		} else {
			LOGGER.info("Either seff or assembly context is not found => stop interpretation for this request.");
		}

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
				Optional.empty());
		final Set<SEFFInterpretationProgressed> appearedEvents = interpreter.doSwitch(context.getProvidedRole());

		return Result.of(appearedEvents);
	}

	/**
	 * Used to interpret the next SEFF that is requested by another seff. For
	 * example, when an External Call action was performed.
	 *
	 * @deprecated This method does not incooperate linking resources where
	 * 				the call might actually be hit. Instead, the responsibility
	 * 				now lies in the ResourceSimulation.
	 */
	@Deprecated
	@Subscribe
	public Result<SEFFInterpretationProgressed> onRequestInitiated(
			final SEFFExternalActionCalled requestInitiated) {
		final GeneralEntryRequest entity = requestInitiated.getEntity();

		final Optional<AssemblyContext> assemblyContext = this.systemRepository
				.findAssemblyContextFromRequiredRole(entity.getRequiredRole());

		if (assemblyContext.isPresent()) {


			final RepositoryInterpreter interpreter = new RepositoryInterpreter(assemblyContext.get(),
					entity.getSignature(),
					null, entity.getUser(), this.systemRepository, entity.getRequestFrom().getCaller());

			/* Interpret the Component of the system. */
			final Set<SEFFInterpretationProgressed> appearedEvents = interpreter
					.doSwitch(assemblyContext.get().getEncapsulatedComponent__AssemblyContext());

			return Result.from(appearedEvents);
		} else {
			return Result.of();
		}
	}

	@Subscribe
	public Result<SEFFInterpretationProgressed> onActiveResourceFinished(
			final ActiveResourceFinished activeResourceFinished) {
		return Result.of(
				new SEFFInterpretationProgressed(activeResourceFinished.getEntity().getSeffInterpretationContext()));
	}
}
