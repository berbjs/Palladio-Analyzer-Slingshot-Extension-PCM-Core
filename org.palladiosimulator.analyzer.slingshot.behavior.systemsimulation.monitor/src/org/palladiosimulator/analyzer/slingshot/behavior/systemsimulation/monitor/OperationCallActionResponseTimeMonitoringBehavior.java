package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.monitor;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFExternalActionCalled;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInterpretationFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInterpretationProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserEntryRequested;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.analyzer.slingshot.monitor.data.entities.ProbeTakenEntity;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.CalculatorRegistered;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.ProbeTaken;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.modelvisited.MeasurementSpecificationVisited;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.modelvisited.MonitorModelVisited;
import org.palladiosimulator.analyzer.slingshot.monitor.utils.probes.EventCurrentSimulationTimeProbe;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Role;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcmmeasuringpoint.AssemblyOperationMeasuringPoint;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.DefaultCalculatorProbeSets;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.probeframework.measurement.RequestContext;

/**
 * A system model monitoring that monitors call actions.
 *
 * What's the fucking difference between {@link SystemOperationMeasuringPoint}
 * and {@link AssemblyOperationMeasuringPoint}?? SystemOpMP refs a system,
 * AssemblyOpMP refs an Assembly. regarding the operations/roles they are equal.
 * Differens probs appears as soon as i got a system with multiple
 * AssemblyContext, cuz then i have access to Ops from all context if i choose
 * the system.
 *
 * for now, lemme stick to AssemblyOpMP, cause it's easier. - with an
 * AssemblRef, i'm closer to the RepositoryComponent, which might be useful?
 *
 * - cannot go for extcall action, because the first call ist not extcall but
 * entrylevel call.
 *
 * start : SeffInterpretationProgressed + CurrentElement "Start" + top level
 * stop : SeffInterpretationProgressed + CurrentElement "Stop" + top level
 *
 *
 *
 * @author Sarah Stiess
 *
 */
@OnEvent(when = MonitorModelVisited.class, then = CalculatorRegistered.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = SEFFExternalActionCalled.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = UserEntryRequested.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = SEFFInterpretationProgressed.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = SEFFInterpretationFinished.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
public class OperationCallActionResponseTimeMonitoringBehavior implements SimulationBehaviorExtension {

	private final IGenericCalculatorFactory calculatorFactory;

	private final Map<String, OperationProbes> userProbesMap = new HashMap<>();

	@Inject
	public OperationCallActionResponseTimeMonitoringBehavior(final IGenericCalculatorFactory calculatorFactory) {
		this.calculatorFactory = calculatorFactory;
	}

	/**
	 * Creates probes if a measuring point for it was specified.
	 *
	 * @param event
	 * @return
	 */
	@Subscribe
	public Result<CalculatorRegistered> onMeasurementSpecificationVisited(final MeasurementSpecificationVisited event) {
		final MeasurementSpecification measurementSpecification = event.getEntity();
		final MeasuringPoint measuringPoint = measurementSpecification.getMonitor().getMeasuringPoint();

		if (measuringPoint instanceof AssemblyOperationMeasuringPoint
				&& MetricDescriptionUtility.metricDescriptionIdsEqual(measurementSpecification.getMetricDescription(),
						MetricDescriptionConstants.RESPONSE_TIME_METRIC)) {
			final Role role = ((AssemblyOperationMeasuringPoint) measuringPoint).getRole();

			if (role instanceof OperationProvidedRole) {

				final OperationProbes userProbes = new OperationProbes();
				this.userProbesMap.put(role.getId(), userProbes);

				final Calculator calculator = this.calculatorFactory.buildCalculator(
						MetricDescriptionConstants.RESPONSE_TIME_METRIC_TUPLE, measuringPoint,
						DefaultCalculatorProbeSets.createStartStopProbeConfiguration(userProbes.operationStartedProbe,
								userProbes.operationFinishedProbe));

				return Result.of(new CalculatorRegistered(calculator));
			}
		}

		return Result.empty();

	}

	@Subscribe
	public Result<ProbeTaken> onOperationCallStarted(final SEFFExternalActionCalled seffProgressed) {

		// PROBLEM : I only know the Required role, not the provided one.

		if (seffProgressed.getEntity().getSignature() instanceof OperationSignature
				&& this.userProbesMap.containsKey(seffProgressed.getEntity().getSignature().getId())) {

			final OperationProbes userProbes = this.userProbesMap
					.get(seffProgressed.getEntity().getSignature().getId());
			userProbes.operationStartedProbe.takeMeasurement(seffProgressed);
			return Result
					.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(userProbes.operationStartedProbe).build()));
		}
		return Result.empty();
	}

//	@Subscribe
//	public Result<ProbeTaken> onOperationCallStarted(final UserEntryRequested seffProgressed) {
//
//		if (this.userProbesMap.containsKey(seffProgressed.getEntity().getOperationProvidedRole().getId())) {
//
//			final OperationProbes userProbes = this.userProbesMap
//					.get(seffProgressed.getEntity().getOperationProvidedRole().getId());
//			userProbes.operationStartedProbe.takeMeasurement(seffProgressed);
//			return Result
//					.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(userProbes.operationStartedProbe).build()));
//		}
//		return Result.empty();
//	}

	@Subscribe
	public Result<ProbeTaken> onOperationCallStarted(final SEFFInterpretationProgressed seffProgressed) {
		// ignore nested SEFFs
		if (seffProgressed.getEntity().getBehaviorContext().isChild()) {
			return Result.empty();
		}

		if (seffProgressed.getEntity().getBehaviorContext().getCurrentProcessedBehavior().getCurrentAction() == null
				|| !(seffProgressed.getEntity().getBehaviorContext().getCurrentProcessedBehavior()
				.getCurrentAction().getPredecessor_AbstractAction() instanceof StartAction)) {
			return Result.empty(); // current action is null?
		}

		// for the system level entry call, the provided role is the outer role.
		if (this.userProbesMap
				.containsKey(seffProgressed.getEntity().getRequestProcessingContext().getProvidedRole().getId())) {
			final OperationProbes userProbes = this.userProbesMap
					.get(seffProgressed.getEntity().getRequestProcessingContext().getProvidedRole().getId());
			userProbes.operationStartedProbe.takeMeasurement(seffProgressed);
			return Result
					.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(userProbes.operationStartedProbe).build()));
		}
		return Result.empty();
	}

	@Subscribe
	public Result<ProbeTaken> onOperationCallFinished(final SEFFInterpretationFinished seffFinished) {
		// ignore nested SEFFs
		if (seffFinished.getEntity().getBehaviorContext().isChild()) {
			return Result.empty();
		}

		// if it's ext call, we dont have the user request. only entry level system call
		// has the user request.
		// actualy, op signature from user request would be wrong here anyway.
		// if i had the provided role though, i could get to the provided op signature.

		if (this.userProbesMap
				.containsKey(seffFinished.getEntity().getRequestProcessingContext().getProvidedRole().getId())) {
			final OperationProbes userProbes = this.userProbesMap
					.get(seffFinished.getEntity().getRequestProcessingContext().getProvidedRole().getId());
			userProbes.operationFinishedProbe.takeMeasurement(seffFinished);
			return Result.of(
					new ProbeTaken(ProbeTakenEntity.builder().withProbe(userProbes.operationFinishedProbe).build()));
		}
		return Result.empty();
	}

	/**
	 * Syntactic sugar to group start and finished probe together.
	 *
	 */
	private static final class OperationProbes {
		private final EventCurrentSimulationTimeProbe operationStartedProbe = new EventCurrentSimulationTimeProbe(
				OperationProbes::passedElement);
		private final EventCurrentSimulationTimeProbe operationFinishedProbe = new EventCurrentSimulationTimeProbe(
				OperationProbes::passedElement);

		private static RequestContext passedElement(final DESEvent desEvent) {
			if (desEvent instanceof SEFFInterpretationFinished) {
				final SEFFInterpretationFinished el = (SEFFInterpretationFinished) desEvent;
				return new RequestContext(el.getEntity().getRequestProcessingContext().getUser().getId());
			}
			if (desEvent instanceof SEFFInterpretationProgressed) {
				final SEFFInterpretationProgressed el = (SEFFInterpretationProgressed) desEvent;
				return new RequestContext(el.getEntity().getRequestProcessingContext().getUser().getId());
			}
			if (desEvent instanceof SEFFExternalActionCalled) {
				final SEFFExternalActionCalled el = (SEFFExternalActionCalled) desEvent;
				return new RequestContext(el.getEntity().getUser().getId());
			}
			if (desEvent instanceof UserEntryRequested) {
				final UserEntryRequested el = (UserEntryRequested) desEvent;
				return new RequestContext(el.getEntity().getUser().getId());
			}
			return RequestContext.EMPTY_REQUEST_CONTEXT;
		}
	}
}
