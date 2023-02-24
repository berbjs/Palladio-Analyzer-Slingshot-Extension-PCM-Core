package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.interpreters;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.RootBehaviorContextHolder;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.user.RequestProcessingContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInterpretationProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.repository.SystemModelRepository;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.common.utils.SimulatedStackHelper;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ComposedStructure;
import org.palladiosimulator.pcm.core.composition.CompositionPackage;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.entity.ComposedProvidingRequiringEntity;
import org.palladiosimulator.pcm.core.entity.EntityPackage;
import org.palladiosimulator.pcm.core.entity.InterfaceProvidingEntity;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.repository.util.RepositorySwitch;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;

import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStackframe;

/**
 * The repository interpreter interprets either a repository or the system model
 * itself. It is based of a Switch which can be used to further iterate the
 * system. The repository interpreter sometimes needs a system model repository
 * (accessor) in order to get further information on the system, such as when a
 * provided role must be accessed.
 * <p>
 * This kind of interpreter will always return a set of events (or an empty
 * set). This interpreter is therefore made for behavior extensions in mind.
 *
 *
 * @author Julijan Katic
 */
public class RepositoryInterpreter extends RepositorySwitch<Set<SEFFInterpretationProgressed>> {

	private static final Logger LOGGER = Logger.getLogger(RepositoryInterpreter.class);

	/** The special assembly context to interpret. */
	private final AssemblyContext assemblyContext;

	/** The provided role from which the interpretation started. */
	private final ProvidedRole providedRole;

	/** A signature of to find the right RDSeff. */
	private final Signature signature;

	/** The context onto which to push stack frames for RDSeffs. */
	private final User user;

	/** The model repository to get more information from the system model. */
	private final SystemModelRepository modelRepository;

	/** The context of the calling seff */
	private final Optional<SEFFInterpretationContext> callerContext;

	/**
	 * Instantiates the interpreter with given information. Depending on the
	 * interpretation, not every parameter must be set (every parameter CAN be
	 * null!).
	 *
	 * @param context         The special assembly context to interpret.
	 * @param signature       A signature to find the right RDSeff.
	 * @param providedRole    The provided role from which the interpretation
	 *                        started.
	 * @param user            The context onto which to push stack frames for
	 *                        RDSeffs.
	 * @param modelRepository The model repository to get more information from the
	 *                        system model.
	 */
	public RepositoryInterpreter(final AssemblyContext context, final Signature signature,
			final ProvidedRole providedRole, final User user, final SystemModelRepository modelRepository,
			final Optional<SEFFInterpretationContext> callerContext) {
		this.assemblyContext = context;
		this.signature = signature;
		this.providedRole = providedRole;
		this.user = user;
		this.modelRepository = modelRepository;
		this.callerContext = callerContext;
	}

	/**
	 * Spawns {@link SeffInterpretationRequested} events with the provided
	 * {@link #signature}.
	 */
	@Override
	public Set<SEFFInterpretationProgressed> caseBasicComponent(final BasicComponent object) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Entering BasicComponent: " + object);
		}

		final SimulatedStackframe<Object> componentParameterStackframe = SimulatedStackHelper
		        .createAndPushNewStackFrame(this.user.getStack(),
		                object.getComponentParameterUsage_ImplementationComponentType(),
		                this.user.getStack().currentStackFrame());
		SimulatedStackHelper.createAndPushNewStackFrame(this.user.getStack(),
		        this.assemblyContext.getConfigParameterUsages__AssemblyContext(), componentParameterStackframe);

		final List<ServiceEffectSpecification> calledSeffs = this
		        .getSeffsForCall(object.getServiceEffectSpecifications__BasicComponent(), this.signature);


		return calledSeffs.stream()
		        .filter(ResourceDemandingSEFF.class::isInstance)
		        .map(ResourceDemandingSEFF.class::cast)
		        .map(rdSeff -> {
		        	/*
		    		 * Define for each SEFF a new request event to be interpreted.
		    		 */
			        final SEFFInterpretationContext context = SEFFInterpretationContext.builder()
			        		.withAssemblyContext(this.assemblyContext)
			        		.withCaller(callerContext)
			        		.withBehaviorContext(new RootBehaviorContextHolder(rdSeff))
			        		.withRequestProcessingContext(RequestProcessingContext.builder()
			        				.withAssemblyContext(this.assemblyContext)
			        				.withProvidedRole(this.providedRole)
			        				.withUser(this.user)
			        				.build())
			        		.build();
			        return new SEFFInterpretationProgressed(context);
		        })
		        .collect(Collectors.toSet());

	}

	/**
	 * Helper method that returns the list of SEFFs that are meant for the
	 * operationSignature.
	 *
	 * @param serviceEffectSpecifications The (Ecore) list of all seffs.
	 * @param operationSignature          The signature which a SEFF should
	 *                                    describe.
	 * @return List of seffs describing {@code operationSignature}.
	 */
	private List<ServiceEffectSpecification> getSeffsForCall(
	        final EList<ServiceEffectSpecification> serviceEffectSpecifications,
	        final Signature operationSignature) {
		assert serviceEffectSpecifications != null && operationSignature != null;
		return serviceEffectSpecifications.stream()
		        .filter(seff -> seff.getDescribedService__SEFF().getId().equals(operationSignature.getId()))
		        .collect(Collectors.toList());
	}

	/**
	 * Interprets the provided role of a system model / repository model. This is
	 * done by looking at the providing entity and looking which events can be
	 * spawned by it.
	 *
	 * If the providedRole belongs to a composed entity (such as the system as a
	 * whole where the user can enter the system), then
	 * {@link #caseComposedProvidingRequiringEntity()} will be called. Otherwise,
	 * the normal {@link #caseBasicComponent()} will be called.
	 */
	@Override
	public Set<SEFFInterpretationProgressed> caseProvidedRole(final ProvidedRole providedRole) {
		LOGGER.debug("Accessing provided role: " + providedRole.getId());

		/* Sometime the providing entity is not defined and therefore must be
		 * found by the system model repository to find the right entity.
		 */
		//if (providedRole.getProvidingEntity_ProvidedRole() == null) {
		//	LOGGER.debug("ProvidedRole does not have the information about its providing entity, find it...");
		//	final InterfaceProvidingEntity foundEntity = this.modelRepository.findProvidingEntity(providedRole);
		//	providedRole.setProvidingEntity_ProvidedRole(foundEntity);
		//}

		return this.doSwitch(providedRole.getProvidingEntity_ProvidedRole());
	}

	/**
	 * The ComposedProvidingRequiringEntity is a special entity (which is typically
	 * the system itself). It often has inner assembly contexts which is connected
	 * to this entity with a delegation connector.
	 *
	 * If such assembly context exists, then the (provided) role of that assembly
	 * context will be interpreted.
	 *
	 */
	@Override
	public Set<SEFFInterpretationProgressed> caseComposedProvidingRequiringEntity(
	        final ComposedProvidingRequiringEntity entity) {

		if (entity != this.providedRole.getProvidingEntity_ProvidedRole()) {
			/*
			 * Interpret entity of provided role only.
			 */
			return Set.of();
		}

		final ProvidedDelegationConnector connectedProvidedDelegationConnector = this.getConnectedProvidedDelegationConnector()
				.orElseThrow(IllegalStateException::new);
		final RepositoryInterpreter repositoryInterpreter = new RepositoryInterpreter(
		        connectedProvidedDelegationConnector.getAssemblyContext_ProvidedDelegationConnector(), this.signature,
		        connectedProvidedDelegationConnector.getInnerProvidedRole_ProvidedDelegationConnector(), this.user,
		        this.modelRepository, Optional.empty());
		return repositoryInterpreter
		        .doSwitch(connectedProvidedDelegationConnector.getInnerProvidedRole_ProvidedDelegationConnector());
	}

	/**
	 * Determines the provided delegation connector which is connected with the
	 * provided role.
	 *
	 * @return the determined provided delegation connector, null otherwise.
	 */
	private Optional<ProvidedDelegationConnector> getConnectedProvidedDelegationConnector() {
		final InterfaceProvidingEntity implementingEntity = this.providedRole.getProvidingEntity_ProvidedRole();
		assert implementingEntity instanceof ComposedStructure;

		final ComposedStructure composedStructure = (ComposedStructure) implementingEntity;

		return composedStructure.getConnectors__ComposedStructure().stream()
				.filter(connector -> connector.eClass() == CompositionPackage.eINSTANCE
						.getProvidedDelegationConnector())
				.map(ProvidedDelegationConnector.class::cast)
				.filter(delegationConnector -> delegationConnector.getOuterProvidedRole_ProvidedDelegationConnector()
						.getId().equals(this.providedRole.getId()))
				.findFirst();

	}

	/**
	 * Overrides the switch in such a way that
	 * {@link ComposedProvidingRequiringEntity}s (and their subtypes) will always
	 * call {@link #caseComposedProvidingRequiringEntity()}. Also, it ensures that
	 * never {@code null} is returned, but an empty set instead.
	 */
	@Override
	public Set<SEFFInterpretationProgressed> doSwitch(final EClass eClass, final EObject eObject) {
		Set<SEFFInterpretationProgressed> result;
		if (EntityPackage.eINSTANCE.getComposedProvidingRequiringEntity().isSuperTypeOf(eClass)) {
			result = this.caseComposedProvidingRequiringEntity((ComposedProvidingRequiringEntity) eObject);
		} else {
			result = super.doSwitch(eClass, eObject);
		}

		if (result == null) {
			result = Set.of();
		}

		return result;
	}
}
