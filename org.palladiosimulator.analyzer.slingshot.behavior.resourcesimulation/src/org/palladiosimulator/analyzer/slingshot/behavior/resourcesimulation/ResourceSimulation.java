package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation;

import static org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality.SINGLE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.WaitingJob;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.AbstractJobEvent;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ActiveResourceStateUpdated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.repository.ResourceEnvironmentAccessor;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active.ActiveResource;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active.ActiveResourceCompoundKey;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active.ActiveResourceTable;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.passive.PassiveResourceCompoundKey;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.passive.PassiveResourceTable;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.passive.SimplePassiveResource;
import org.palladiosimulator.analyzer.slingshot.behavior.spd.data.ModelAdjusted;
import org.palladiosimulator.analyzer.slingshot.behavior.spd.data.adjustment.ResourceEnvironmentChange;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest.ResourceType;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.ActiveResourceFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.PassiveResourceAcquired;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.PassiveResourceReleased;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.ResourceDemandRequested;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
//import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.events.ModelAdjusted;
import org.palladiosimulator.analyzer.slingshot.core.events.SimulationFinished;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.PassiveResource;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentFactory;

import de.uka.ipd.sdq.simucomframework.variables.StackContext;

/**
 * The resource simulation behavior initializes all the available resources on
 * start and will listen to requests for the simulation.
 *
 * @author Julijan Katic
 */
@OnEvent(when = SimulationFinished.class, then = {})
@OnEvent(when = JobInitiated.class, then = { JobProgressed.class,
		ActiveResourceStateUpdated.class }, cardinality = EventCardinality.MANY)
@OnEvent(when = JobProgressed.class, then = { AbstractJobEvent.class,
		ActiveResourceStateUpdated.class }, cardinality = EventCardinality.MANY)
@OnEvent(when = JobFinished.class, then = ActiveResourceFinished.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = PassiveResourceReleased.class, then = PassiveResourceAcquired.class, cardinality = EventCardinality.MANY)
@OnEvent(when = ResourceDemandRequested.class, then = { JobInitiated.class,
		PassiveResourceAcquired.class }, cardinality = SINGLE)
@OnEvent(when = ModelAdjusted.class, then = {})
public class ResourceSimulation implements SimulationBehaviorExtension {

	private static final Logger LOGGER = Logger.getLogger(ResourceSimulation.class);

	private final Allocation allocation;
	private final ResourceEnvironmentAccessor resourceEnvironmentAccessor;

	private final ActiveResourceTable resourceTable;
	private final PassiveResourceTable passiveResourceTable;

	@Inject
	public ResourceSimulation(final Allocation allocation) {
		this.allocation = allocation;
		this.resourceEnvironmentAccessor = new ResourceEnvironmentAccessor(allocation);
		this.resourceTable = new ActiveResourceTable();
		this.passiveResourceTable = new PassiveResourceTable();
		this.init();
	}

	public void init() {
		this.resourceTable.buildModel(this.allocation);
		this.passiveResourceTable.buildTable(this.allocation);
	}

	@Subscribe
	public Result<AbstractSimulationEvent> onResourceDemandRequested(final ResourceDemandRequested resourceDemandRequested) {
		final ResourceDemandRequest request = resourceDemandRequested.getEntity();

		if (request.getResourceType() == ResourceType.ACTIVE) {
			return Result.of(this.initiateActiveResource(request));
		} else {
			return Result.of(this.initiatePassiveResource(request));
		}
	}

	/**
	 * @param request
	 */
	private Optional<PassiveResourceAcquired> initiatePassiveResource(final ResourceDemandRequest request) {
		final PassiveResource passiveResource = request.getPassiveResource().get();
		final AssemblyContext assemblyContext = request.getAssemblyContext();
		final Optional<SimplePassiveResource> passiveResourceInstance = this.passiveResourceTable
				.getPassiveResource(PassiveResourceCompoundKey.of(passiveResource, assemblyContext));

		if (passiveResourceInstance.isPresent()) {
			final WaitingJob waitingJob = this.createWaitingJob(request, passiveResource);
			return passiveResourceInstance.get().acquire(waitingJob);
		}
		return Optional.empty();
	}

	/**
	 * @param request
	 * @return
	 */
	private JobInitiated initiateActiveResource(final ResourceDemandRequest request) {
		final double demand = StackContext.evaluateStatic(
				request.getParametricResourceDemand().getSpecification_ParametericResourceDemand()
						.getSpecification(),
				Double.class, request.getUser().getStack().currentStackFrame());

		final AllocationContext context = this.allocation.getAllocationContexts_Allocation().stream()
				.filter(c -> c.getAssemblyContext_AllocationContext().getId().equals(request.getAssemblyContext().getId()))
				.findFirst()
				.orElseThrow(() -> new NoSuchElementException("No allocation context found which contains an assembly context with id #" + request.getAssemblyContext().getId()));

		final Job job = Job.builder()
				.withDemand(demand)
				.withId(UUID.randomUUID().toString())
				.withProcessingResourceType(
						request.getParametricResourceDemand().getRequiredResource_ParametricResourceDemand())
				.withRequest(request)
				.withAllocationContext(context)
				.build();

		return new JobInitiated(job, 0);
	}

	/**
	 * @param request
	 * @param passiveResource
	 * @return
	 */
	private WaitingJob createWaitingJob(final ResourceDemandRequest request, final PassiveResource passiveResource) {
		//TODO::FIX ME!
		final long demand = StackContext.evaluateStatic(
				request.getParametricResourceDemand().getSpecification_ParametericResourceDemand()
						.getSpecification(),
				Long.class, request.getUser().getStack().currentStackFrame());

		final WaitingJob waitingJob = WaitingJob.builder()
				.withPassiveResource(passiveResource)
				.withRequest(request)
				.withDemand(demand)
				.build();
		return waitingJob;
	}

	@Subscribe
	public Result<AbstractJobEvent> onJobInitiated(final JobInitiated jobInitiated) {
		final Job job = jobInitiated.getEntity();
		final ActiveResourceCompoundKey id = new ActiveResourceCompoundKey(
				job.getAllocationContext().getResourceContainer_AllocationContext(), job.getProcessingResourceType());

		final Optional<ActiveResource> activeResource = this.resourceTable.getActiveResource(id);

		if (activeResource.isEmpty()) {
			LOGGER.error("No such active resource found! " + id.toString());
			return Result.of();
		}

		return Result.of(activeResource.get().onJobInitiated(jobInitiated));
	}

	@Subscribe
	public Result<PassiveResourceAcquired> onPassiveResourceReleased(
			final PassiveResourceReleased passiveResourceReleased) {
		final ResourceDemandRequest entity = passiveResourceReleased.getEntity();
		final Optional<SimplePassiveResource> passiveResource = this.passiveResourceTable.getPassiveResource(
				PassiveResourceCompoundKey.of(entity.getPassiveResource().get(), entity.getAssemblyContext()));

		if (passiveResource.isEmpty()) {
			LOGGER.error("No such passive resource found!");
			return Result.of();
		}

		final WaitingJob waitingJob = this.createWaitingJob(entity, entity.getPassiveResource().get());
		return Result.of(passiveResource.get().release(waitingJob));
	}

	@Subscribe
	public Result<AbstractJobEvent> onJobProgressed(final JobProgressed jobProgressed) {
		final Job job = jobProgressed.getEntity();
		final ActiveResourceCompoundKey id = ActiveResourceCompoundKey.of(
				job.getAllocationContext().getResourceContainer_AllocationContext(), job.getProcessingResourceType());

		final Optional<ActiveResource> activeResource = this.resourceTable.getActiveResource(id);

		if (activeResource.isEmpty()) {
			LOGGER.error("No such resource found!");
			return Result.of();
		}

		return Result.of(activeResource.get().onJobProgressed(jobProgressed));
	}

	/**
	 * This event handler will give a global response event that the certain request
	 * is finished.
	 *
	 * @return Set containing {@link ActiveResourceFinished}.
	 */
	@Subscribe
	public Result<ActiveResourceFinished> onJobFinished(final JobFinished evt) {
		return Result.of(new ActiveResourceFinished(evt.getEntity().getRequest(), 0));
	}

	@Subscribe
	public Result<?> onModelAdjusted(final ModelAdjusted modelChanged) {
		modelChanged.getChanges().stream()
								 .filter(change -> change instanceof ResourceEnvironmentChange)
								 .map(ResourceEnvironmentChange.class::cast)
								 .forEach(this::changeActiveResourceTableFromModelChange);
		
		return Result.empty();
	}
	
	private void changeActiveResourceTableFromModelChange(final ResourceEnvironmentChange change) {
		change.getNewResourceContainers().forEach(newContainer -> 
			this.resourceTable.createActiveResourcesFromResourceContainer(newContainer));
		
		change.getDeletedResourceContainers().forEach(deletedContainer ->
			this.resourceTable.removeActiveResources(deletedContainer));
	}

	/*
	 * TODO: When GeneralEntryRequest,
	 * 	1. (System): Find appropriate assembly context -> AllocationContextRequested
	 *  2. (Resource): Check whether AsC is located somewhere else
	 *  	2.1 (Resource): If yes, get the linking resource
	 *  	2.2 (Resource): According to the LR, simulate a band-width limit by adding simu time
	 *  3. (Resource): Afterwards, send AppropriateResourceACFound with the specified delay
	 *  4. (System): Continue with the actual
	 */

	public Result<?> onAllocationContextRequested(final AssemblyContext from, final AssemblyContext to) {
		// Precondition: from and to are somehow connected to each other
		final AllocationContext fromAlC = this.resourceEnvironmentAccessor.findResourceContainerOfComponent(from).orElseThrow();
		final AllocationContext toAlC = this.resourceEnvironmentAccessor.findResourceContainerOfComponent(to).orElseThrow();

		// We now find the LinkingResource which connects both containers
		final LinkingResource linkingResource = this.allocation.getTargetResourceEnvironment_Allocation()
					   .getLinkingResources__ResourceEnvironment()
					   .stream()
					   .filter(lr -> lr.getConnectedResourceContainers_LinkingResource()
							   		   .stream()
							   		   .anyMatch(rc -> rc.getId().equals(fromAlC.getResourceContainer_AllocationContext().getId())) &&
							   		 lr.getConnectedResourceContainers_LinkingResource()
							   		   .stream()
							   		   .anyMatch(rc -> rc.getId().equals(toAlC.getResourceContainer_AllocationContext().getId()))
							   )
					   .findAny()
					   .orElseThrow();

		final double failureProbability = linkingResource.getCommunicationLinkResourceSpecifications_LinkingResource().getFailureProbability();
		if (Math.random() < failureProbability) {
			// Simulate a failing connection, i.e. LinkFailed
		}

		final PCMRandomVariable latencyRV = linkingResource.getCommunicationLinkResourceSpecifications_LinkingResource().getLatency_CommunicationLinkResourceSpecification();
		final double latency = StackContext.evaluateStatic(latencyRV.getSpecification(), Double.class);

		// Now, return AppropriateResourceFound with latency
		return Result.of();
	}

	/**
	 * Clears the contexts as soon as the simulation has finished.
	 *
	 * @return an empty set.
	 */
	@Subscribe
	public void onSimulationFinished(final SimulationFinished simulationFinished) {
		setupAndSaveResourceModel();
		
		this.resourceTable.clearResourcesFromJobs();
		this.passiveResourceTable.clearResourcesFromJobs();
	}
	
	/*
	 * Only for debugging!
	 */
	private void setupAndSaveResourceModel() {
		final ResourceSet rs = new ResourceSetImpl();
		final Resource reResource = createResource("platform:/resource/RemoteMeasuringMosaic/rm_output.resourceenvironment", rs);
		reResource.getContents().add(allocation.getTargetResourceEnvironment_Allocation());
		saveResource(reResource);
	}
	
	private Resource createResource(final String outputFile, final ResourceSet rs) { 
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("resourceenvironment", new XMLResourceFactoryImpl());
		final URI uri = URI.createURI(outputFile);
		final Resource resource = rs.createResource(uri);
		((ResourceImpl) resource).setIntrinsicIDToEObjectMap(new HashMap<>());
		return resource;
	}
	
	private void saveResource(final Resource resource) {
		final Map saveOptions = ((XMLResource) resource).getDefaultSaveOptions();
		saveOptions.put(XMLResource.OPTION_CONFIGURATION_CACHE, Boolean.TRUE);
		saveOptions.put(XMLResource.OPTION_USE_CACHED_LOOKUP_TABLE, new ArrayList<>());
		
		try {
			resource.save(saveOptions);
		} catch (final IOException e) {
			LOGGER.error(e);
		}
	}
}
