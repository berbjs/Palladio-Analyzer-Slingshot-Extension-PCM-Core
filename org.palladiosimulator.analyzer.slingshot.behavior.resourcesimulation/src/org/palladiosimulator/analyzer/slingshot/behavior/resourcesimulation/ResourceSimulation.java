package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation;

import static org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality.SINGLE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
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
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.ActiveJob;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.LinkingJob;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.WaitingJob;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.AbstractJobEvent;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ActiveResourceStateUpdated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobAborted;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.PassiveResourceStateUpdated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ResourceDemandCalculated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.repository.ResourceEnvironmentAccessor;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active.ActiveResource;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active.ActiveResourceCompoundKey;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active.ActiveResourceTable;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.linking.LinkingResourceTable;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.linking.SimulatedLinkingResource;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.passive.PassiveResourceCompoundKey;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.passive.PassiveResourceTable;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.passive.SimplePassiveResource;
import org.palladiosimulator.analyzer.slingshot.behavior.spd.data.ModelAdjusted;
import org.palladiosimulator.analyzer.slingshot.behavior.spd.data.adjustment.ResourceEnvironmentChange;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.CallOverWireRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest.ResourceType;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.AbstractResourceRequestEvent;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.ActiveResourceFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.CallOverWireAborted;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.CallOverWireSucceeded;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.ExternalCallRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.PassiveResourceAcquired;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.PassiveResourceReleased;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.ResourceDemandRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
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
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.PassiveResource;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;

import de.uka.ipd.sdq.simucomframework.variables.StackContext;
import de.uka.ipd.sdq.simucomframework.variables.converter.NumberConverter;

/**
 * The resource simulation behavior initializes all the available resources on
 * start and will listen to requests for the simulation.
 *
 * @author Julijan Katic
 */
@OnEvent(when = SimulationFinished.class, then = {})
@OnEvent(when = JobInitiated.class, then = { JobProgressed.class,
		ActiveResourceStateUpdated.class, ResourceDemandCalculated.class }, cardinality = EventCardinality.MANY)
@OnEvent(when = JobProgressed.class, then = { AbstractJobEvent.class,
		ActiveResourceStateUpdated.class, ResourceDemandCalculated.class }, cardinality = EventCardinality.MANY)
@OnEvent(when = JobFinished.class, then = { ActiveResourceFinished.class,
		CallOverWireSucceeded.class }, cardinality = EventCardinality.SINGLE)
@OnEvent(when = PassiveResourceReleased.class, then = PassiveResourceAcquired.class, cardinality = EventCardinality.MANY)
@OnEvent(when = ResourceDemandRequested.class, then = { JobInitiated.class,
		PassiveResourceAcquired.class }, cardinality = SINGLE)
@OnEvent(when = ModelAdjusted.class, then = {})
@OnEvent(when = ExternalCallRequested.class, then = { JobInitiated.class,
		CallOverWireSucceeded.class }, cardinality = EventCardinality.SINGLE)
@OnEvent(when = JobAborted.class, then = CallOverWireAborted.class, cardinality = SINGLE)
public class ResourceSimulation implements SimulationBehaviorExtension {

	private static final Logger LOGGER = Logger.getLogger(ResourceSimulation.class);

	private final Allocation allocation;
	private final ResourceEnvironmentAccessor resourceEnvironmentAccessor;

	private final ActiveResourceTable resourceTable;
	private final PassiveResourceTable passiveResourceTable;
	private final LinkingResourceTable linkingResourceTable;

	@Inject
	public ResourceSimulation(final Allocation allocation) {
		this.allocation = allocation;
		this.resourceEnvironmentAccessor = new ResourceEnvironmentAccessor(allocation);
		this.resourceTable = new ActiveResourceTable();
		this.passiveResourceTable = new PassiveResourceTable();
		this.linkingResourceTable = new LinkingResourceTable();
		this.init();
	}

	public void init() {
		this.resourceTable.buildModel(this.allocation);
		this.passiveResourceTable.buildTable(this.allocation);
		this.linkingResourceTable.buildTable(this.allocation);
	}

	@Subscribe
	public Result<AbstractSimulationEvent> onResourceDemandRequested(final ResourceDemandRequested resourceDemandRequested) {
		final ResourceDemandRequest request = resourceDemandRequested.getEntity();

		if (request.getResourceType() == ResourceType.ACTIVE) {
			return Result.of(this.initiateActiveResource(request));
		}
		return Result.of(this.initiatePassiveResource(request));
	}

	/**
	 * @param request
	 */
	private Set<AbstractResourceRequestEvent> initiatePassiveResource(final ResourceDemandRequest request) {
		final PassiveResource passiveResource = request.getPassiveResource().get();
		final AssemblyContext assemblyContext = request.getAssemblyContext();
		final Optional<SimplePassiveResource> passiveResourceInstance = this.passiveResourceTable
				.getPassiveResource(PassiveResourceCompoundKey.of(passiveResource, assemblyContext));

		if (passiveResourceInstance.isPresent()) {
			final WaitingJob waitingJob = this.createWaitingJob(request, passiveResource);
			final Optional<PassiveResourceAcquired> aquired = passiveResourceInstance.get().acquire(waitingJob);
			if (aquired.isPresent()) {
				return Set.of(aquired.get(), new PassiveResourceStateUpdated(request,
						passiveResourceInstance.get().getCurrentlyAvailable()));
			}
		}
		return Set.of();
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

		final Job job = ActiveJob.builder()
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
		if (job instanceof ActiveJob) {
			final ActiveJob activeJob = (ActiveJob) job;
			final ActiveResourceCompoundKey id = new ActiveResourceCompoundKey(
					activeJob.getAllocationContext().getResourceContainer_AllocationContext(),
					activeJob.getProcessingResourceType());

			final Optional<ActiveResource> activeResource = this.resourceTable.getActiveResource(id);

			if (activeResource.isEmpty()) {
				LOGGER.error("No such active resource found! " + id.toString());
				return Result.of();
			}

			return Result.of(activeResource.get().onJobInitiated(jobInitiated));
		}
		if (job instanceof LinkingJob) {
			final LinkingJob linkingJob = (LinkingJob) job;
			final Optional<SimulatedLinkingResource> linkingResource = this.linkingResourceTable
					.getResourceById(linkingJob.getLinkingResource().getId());

			if (linkingResource.isEmpty()) {
				LOGGER.error("No such resource found!");
				return Result.of();
			}

			LOGGER.info("A linking job has started with id " + linkingJob.getId() + " and demand (without latency) "
					+ linkingJob.getDemand());
			final Set<AbstractJobEvent> jobs = linkingResource.get().onJobInitiated(jobInitiated);
			LOGGER.debug("Size: " + jobs.size());
			jobs.forEach(j -> LOGGER.debug("Job is of type: " + j.getClass().getName() + " and id " + j.getId()));
			return Result.of(jobs);
		}

		return Result.empty();
	}

	@Subscribe
	public Result<AbstractResourceRequestEvent> onPassiveResourceReleased(
			final PassiveResourceReleased passiveResourceReleased) {
		final ResourceDemandRequest entity = passiveResourceReleased.getEntity();
		final Optional<SimplePassiveResource> passiveResource = this.passiveResourceTable.getPassiveResource(
				PassiveResourceCompoundKey.of(entity.getPassiveResource().get(), entity.getAssemblyContext()));

		if (passiveResource.isEmpty()) {
			LOGGER.error("No such passive resource found!");
			return Result.of();
		}

		final WaitingJob waitingJob = this.createWaitingJob(entity, entity.getPassiveResource().get());

		final Set<AbstractResourceRequestEvent> newEvents = new HashSet<>();
		newEvents.addAll(passiveResource.get().release(waitingJob));
		newEvents.add(new PassiveResourceStateUpdated(entity, passiveResource.get().getCurrentlyAvailable()));

		return Result.of(newEvents);
	}

	@Subscribe
	public Result<AbstractJobEvent> onJobProgressed(final JobProgressed jobProgressed) {
		final Job job = jobProgressed.getEntity();

		if (job instanceof ActiveJob) {
			final ActiveJob activeJob = (ActiveJob) job;
			final ActiveResourceCompoundKey id = ActiveResourceCompoundKey.of(
					activeJob.getAllocationContext().getResourceContainer_AllocationContext(),
					activeJob.getProcessingResourceType());

			final Optional<ActiveResource> activeResource = this.resourceTable.getActiveResource(id);

			if (activeResource.isEmpty()) {
				LOGGER.error("No such resource found!");
				return Result.of();
			}

			return Result.of(activeResource.get().onJobProgressed(jobProgressed));
		}
		if (job instanceof LinkingJob) {
			final LinkingJob linkingJob = (LinkingJob) job;
			final Optional<SimulatedLinkingResource> linkingResource = this.linkingResourceTable.getResourceById(linkingJob.getLinkingResource().getId());
			
			if (linkingResource.isEmpty()) {
				LOGGER.error("No such resource found!");
				return Result.of();
			}

			LOGGER.info("A linking job has been progressed: " + linkingJob.getId() + " with demand "
					+ linkingJob.getDemand());
			return Result.of(linkingResource.get().onJobProgressed(jobProgressed));
		}

		return Result.empty();
	}

	/**
	 * This event handler will give a global response event that the certain request
	 * is finished.
	 *
	 * @return Set containing {@link ActiveResourceFinished}.
	 */
	@Subscribe
	public Result<?> onJobFinished(final JobFinished evt) {
		if (evt.getEntity() instanceof ActiveJob) {
			final ActiveJob activeJob = (ActiveJob) evt.getEntity();
			return Result.of(new ActiveResourceFinished(activeJob.getRequest(), 0));
		}
		if (evt.getEntity() instanceof LinkingJob) {
			/* This is just for Debugging Purposes */
			final LinkingJob linkingJob = (LinkingJob) evt.getEntity();
			LOGGER.info("The linking job with id " + linkingJob.getId() + " has finished");
			return Result.of(new CallOverWireSucceeded(linkingJob.getRequest()));
		}

		return Result.empty();
	}

	@Subscribe
	public Result<?> onJobAborted(final JobAborted aborted) {
		if (aborted.getEntity() instanceof LinkingJob) {
			final LinkingJob linkingJob = (LinkingJob) aborted.getEntity();
			return Result.of(new CallOverWireAborted(linkingJob.getRequest()));
		}

		return Result.empty();
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

	@Subscribe
	public Result<?> onSEFFExternalCallAction(final ExternalCallRequested externalCallRequested) {
		final AllocationContext fromAlC = this.resourceEnvironmentAccessor
				.findResourceContainerOfComponent(externalCallRequested.getRequest().getFrom()).orElseThrow();
		final AllocationContext toAlC = this.resourceEnvironmentAccessor
				.findResourceContainerOfComponent(externalCallRequested.getRequest().getTo()).orElseThrow();

		/* Check if they are on the same resource; if so, no call over wire required */
		if (fromAlC.getResourceContainer_AllocationContext().getId()
				.equals(toAlC.getResourceContainer_AllocationContext().getId())) {
			LOGGER.info("Both components lie on the same resource container -> no call over wire required.");
			return Result.of(new CallOverWireSucceeded(externalCallRequested.getRequest()));
		}

		final List<SimulatedLinkingResource> linkingResources = this.linkingResourceTable.findLinkingResourceBetweenContainers(fromAlC.getResourceContainer_AllocationContext(), toAlC.getResourceContainer_AllocationContext());
		return Result.of(linkingResources.stream()

				/* For now, it doesn't matter which linking resource we use */
				.findAny()

				/* For this one linking resource, create the job with the initial demand */
				.map(lr -> createLinkingJobFromResource(lr.getLinkingResource(),
						externalCallRequested.getRequest().getUser(), externalCallRequested.getRequest()))
				
				/* If a linking resource exists, create a job for that */
				.map(job -> new JobInitiated(job, 0.0))
				
				/* Otherwise, the list is empty if they are not connected */
				.orElseThrow(() -> new IllegalArgumentException(
						"The resource containers of the assembly contexts are not directly connected by a linking resource")));
	}

	/**
	 * Sums up all the BYTESIZE values from the user's current stackframe which is
	 * going to be sent over the wire, and uses this as a demand for a new
	 * {@link LinkingJob}. If there are no such information, the default demand will
	 * be 0. However, there might still be a latency on the linking resource.
	 * 
	 * @param linkingResource The linking resource on which the job will be.
	 * @param user            The user that is going to make the call.
	 * @return A new linking job
	 */
	private LinkingJob createLinkingJobFromResource(final LinkingResource linkingResource, final User user,
			final CallOverWireRequest request) {
		final double demand = request.getVariablesToConsider().getContents().stream()
				.filter(entry -> entry.getKey().endsWith("BYTESIZE"))
				.mapToDouble(entry -> NumberConverter.toDouble(entry.getValue())).sum();

		return new LinkingJob(UUID.randomUUID().toString(), demand, linkingResource, request);
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
		this.linkingResourceTable.clearResourcesFromJobs();
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
