package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.monitor;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFModelPassedElement;
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
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Role;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;
import org.palladiosimulator.pcmmeasuringpoint.AssemblyOperationMeasuringPoint;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.DefaultCalculatorProbeSets;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.probeframework.measurement.RequestContext;

/**
 * A system model monitoring that monitors call actions.
 *
 * There are two MeasuringPoint for assemblies, namely
 * {@link SystemOperationMeasuringPoint} and
 * {@link AssemblyOperationMeasuringPoint}. The former references an entire
 * system, while the latter reference a specific assembly. However, both have a
 * reference to the role ({@link ProvidedRole} or it won't work) to be measured
 * at.
 *
 * This Monitor uses {@link AssemblyOperationMeasuringPoint}.
 *
 * SEFFs might be nested, but nested SEFFs do not belong to a call of their own,
 * thus this monitor ignores all SEFFs with parents.
 *
 * Depending on whether an operation gets called by another assembly
 * (ExternalCall) or from the UsageModel (EntryLevelSystemCall), the
 * {@link ProvidedRole} in the {@link SEFFInterpretationContext} might differ.
 * However, it shall be upon the user to specify the correct role in the
 * measuring point.
 *
 *
 * @author Sarah Stiess
 *
 */
@OnEvent(when = MonitorModelVisited.class, then = CalculatorRegistered.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = SEFFModelPassedElement.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
public class OperationCallActionResponseTimeMonitoringBehavior implements SimulationBehaviorExtension {

	private final IGenericCalculatorFactory calculatorFactory;
	private final Map<AssemblyOperationCompoundKey, OperationProbes> userProbesMap = new HashMap<>();

	@Inject
	public OperationCallActionResponseTimeMonitoringBehavior(final IGenericCalculatorFactory calculatorFactory) {
		this.calculatorFactory = calculatorFactory;
	}

	/**
	 * Creates probes for {@link AssemblyOperationMeasuringPoint}s.
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
				final AssemblyOperationCompoundKey key = AssemblyOperationCompoundKey.of(((AssemblyOperationMeasuringPoint) measuringPoint).getAssembly(), (OperationProvidedRole) role);
				this.userProbesMap.put(key, userProbes);

				final Calculator calculator = this.calculatorFactory.buildCalculator(
						MetricDescriptionConstants.RESPONSE_TIME_METRIC_TUPLE, measuringPoint,
						DefaultCalculatorProbeSets.createStartStopProbeConfiguration(userProbes.operationStartedProbe,
								userProbes.operationFinishedProbe));

				return Result.of(new CalculatorRegistered(calculator));
			}
		}

		return Result.empty();

	}

	@Subscribe(reified = StartAction.class)
	public Result<ProbeTaken> onOperationCallStarted(final SEFFModelPassedElement<StartAction> seffStarted) {
		if (seffStarted.getContext().getBehaviorContext().isChild()) {
			return Result.empty();
		}

		final ProvidedRole role = seffStarted.getContext().getRequestProcessingContext().getProvidedRole();
		final AssemblyOperationCompoundKey key = AssemblyOperationCompoundKey
				.of(seffStarted.getContext().getAssemblyContext(), role);

		if (role instanceof OperationProvidedRole && this.userProbesMap.containsKey(key)) {
			final OperationProbes userProbes = this.userProbesMap.get(key);
			userProbes.operationStartedProbe.takeMeasurement(seffStarted);
			return Result
					.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(userProbes.operationStartedProbe).build()));
		}
		return Result.empty();
	}

	@Subscribe(reified = StopAction.class)
	public Result<ProbeTaken> onOperationCallStopped(final SEFFModelPassedElement<StopAction> seffFinished) {
		if (seffFinished.getContext().getBehaviorContext().isChild()) {
			return Result.empty();
		}

		final ProvidedRole role = seffFinished.getContext().getRequestProcessingContext().getProvidedRole();
		final AssemblyOperationCompoundKey key = AssemblyOperationCompoundKey
				.of(seffFinished.getContext().getAssemblyContext(), role);

		if (role instanceof OperationProvidedRole && this.userProbesMap.containsKey(key)) {
			final OperationProbes userProbes = this.userProbesMap.get(key);
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
			if (desEvent instanceof SEFFModelPassedElement<?>) {
				final SEFFModelPassedElement<?> el = (SEFFModelPassedElement<?>) desEvent;
				
				if(el.getContext().getCaller().isPresent()) {
					return new RequestContext(el.getContext().getRequestProcessingContext().getUser().getId()+el.getContext().getCaller().get().hashCode());
				} else {
					return new RequestContext(el.getContext().getRequestProcessingContext().getUser().getId());	
				}
				
			}
			return RequestContext.EMPTY_REQUEST_CONTEXT;
		}
	}
}
