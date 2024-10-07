package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation;

import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.ForkBehaviorContextHolder;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.InfrastructureCallsContextHolder;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.SeffBehaviorContextHolder;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.CallOverWireRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.PassiveResourceAcquired;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFChildInterpretationStarted;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInterpretationFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInterpretationProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInterpreted;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.interpreters.SeffInterpreter;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserRequestFinished;
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;

import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStack;

/**
 * This behavior module both interprets and generates events specifically for
 * SEFFs.
 *
 * @author Julijan Katic, Floriment Klinaku, Sarah Stiess
 */
@OnEvent(when = SEFFInterpretationProgressed.class, then = SEFFInterpreted.class, cardinality = EventCardinality.MANY)
@OnEvent(when = SEFFInterpretationFinished.class, then = { SEFFInterpretationProgressed.class,
		UserRequestFinished.class }, cardinality = EventCardinality.SINGLE)
@OnEvent(when = SEFFChildInterpretationStarted.class, then = SEFFInterpreted.class, cardinality = EventCardinality.MANY)
@OnEvent(when = PassiveResourceAcquired.class, then = SEFFInterpreted.class, cardinality = EventCardinality.MANY)
public class SeffSimulationBehavior implements SimulationBehaviorExtension {

	private static final Logger LOGGER = Logger.getLogger(SeffSimulationBehavior.class);

	@Subscribe
	public Result<SEFFInterpreted> onSeffInterpretationProgressed(final SEFFInterpretationProgressed progressed) {
		final SeffInterpreter interpreter = new SeffInterpreter(progressed.getEntity());
		final SeffBehaviorContextHolder contextHolder = progressed.getEntity().getBehaviorContext();

		if (contextHolder instanceof InfrastructureCallsContextHolder) {
			if (contextHolder.hasFinished()) {
				// continue in parent -> a follow up SEFFInterpretationProgressed in the parent
				LOGGER.info("progression to parent of infra");
				return Result.of(continueInParent(progressed.getEntity()));
			}
		}

		final Set<SEFFInterpreted> events = interpreter.doSwitch(contextHolder.getNextAction());
		return Result.of(events);
	}

	@Subscribe
	public Result<SEFFInterpreted> onPassiveResourceAcquired(final PassiveResourceAcquired passiveResourceAcquired) {
		final SeffInterpreter interpreter = new SeffInterpreter(
				passiveResourceAcquired.getEntity().getSeffInterpretationContext());
		final Set<SEFFInterpreted> events = interpreter.doSwitch(passiveResourceAcquired.getEntity()
				.getSeffInterpretationContext().getBehaviorContext().getNextAction());
		return Result.of(events);
	}

	@Subscribe
	public Result<SEFFInterpreted> onSEFFChildInterpretationStarted(
			final SEFFChildInterpretationStarted seffChildInterpretationStarted) {
		final SeffInterpreter interpreter = new SeffInterpreter(seffChildInterpretationStarted.getEntity());
		final Set<SEFFInterpreted> events = interpreter
				.doSwitch(seffChildInterpretationStarted.getEntity().getBehaviorContext().getNextAction());
		return Result.of(events);
	}

	@Subscribe
	public Result<AbstractSimulationEvent> onSEFFInterpretationFinished(final SEFFInterpretationFinished finished) {
		final SEFFInterpretationContext entity = finished.getEntity();
		final Result<AbstractSimulationEvent> result;
		/*
		 * If the interpretation is finished in a SEFF that was nested into or called
		 * from another SEFF, continue there. Otherwise, the SEFF comes from a User
		 * request.
		 */
		if (entity.getBehaviorContext() instanceof ForkBehaviorContextHolder) {

			final ForkBehaviorContextHolder fb = (ForkBehaviorContextHolder) entity.getBehaviorContext();

			if (!entity.getBehaviorContext().hasFinished()) {
				LOGGER.info("A forked behavior has finished, but not all");
				result = Result.of();
			} else if (fb.isProcessed()) {
				result = Result.of();
			} else {
				LOGGER.debug("return to parent - from forked");
				fb.markProcessed();
				result = Result.of(this.continueInParent(entity));
			}
		} else if (!entity.getBehaviorContext().hasFinished()) {
			LOGGER.debug("repeat scenario");
			result = Result.of(this.repeat(entity));
		} else if (entity.getBehaviorContext().isChild()) { // go to parents first, only go to caller if no parent.
			LOGGER.debug("return to parent");
			result = Result.of(this.continueInParent(entity));
		} else if (entity.getCaller().isPresent()) {
			LOGGER.debug("return to caller");
			result = Result.of(this.continueInCaller(entity));
		} else {
			LOGGER.debug("finish request");
			result = Result.of(this.finishUserRequest(entity));
		}

		return result;
	}

	/**
	 * @param entity
	 * @return
	 */
	private UserRequestFinished finishUserRequest(final SEFFInterpretationContext entity) {
		final UserRequest userRequest = entity.getRequestProcessingContext().getUserRequest();
		final UserInterpretationContext userInterpretationContext = entity.getRequestProcessingContext()
				.getUserInterpretationContext().update().withResultFrame(entity.getCurrentResultStackframe()).build();

		this.cleanUpComponentParameterStackFrames(entity.getRequestProcessingContext().getUser());

		return new UserRequestFinished(userRequest, userInterpretationContext);
	}

	/**
	 * @param entity
	 * @return
	 */
	private SEFFInterpretationProgressed continueInParent(final SEFFInterpretationContext entity) {
		final SEFFInterpretationContext seffInterpretationContext = entity.getParent()
				.orElseThrow(() -> new IllegalStateException("Every child context must have a parent"));

		return new SEFFInterpretationProgressed(seffInterpretationContext);
	}

	/**
	 * If the caller was from another component, then we need to return over the
	 * wire. Thus, we create a reply to a call over wire request and first simulate
	 * the call.
	 */
	private AbstractSimulationEvent continueInCaller(final SEFFInterpretationContext entity) {

		this.cleanUpComponentParameterStackFrames(entity.getRequestProcessingContext().getUser());

		return entity.getCallOverWireRequest()
				.map(cowReq -> cowReq.createReplyRequest(entity.getCurrentResultStackframe()))
				.map(CallOverWireRequested::new)
				.map(AbstractSimulationEvent.class::cast) // Needed for the type check
				.orElseGet(() -> {
					LOGGER.info("It seems that the call was not over a wire, so proceed with normal progression");
					return new SEFFInterpretationProgressed(entity.getCaller().get());
				});
	}

	/**
	 * Pops the 2 (3) topmost frames from the given user's stack.
	 *
	 * These frames are the component parameter frames. They are pushed onto the
	 * stack when entering a new Component with an {@link ExternalCallAction} or a
	 * {@link EntryLevelSystemCall}. Upon leaving (i.e. now) they must be popped
	 * from the stack.
	 *
	 * C.f. Section 4.4.5 Composite Structures of Steffen Becker's PhD Thesis for
	 * more details.
	 *
	 * @param user user whose stack will be cleaned up.
	 */
	private void cleanUpComponentParameterStackFrames(final User user) {
		final SimulatedStack<Object> stack = user.getStack();
		// stack.removeStackFrame(); // pop domain expert
		stack.removeStackFrame(); // pop software architect
		stack.removeStackFrame(); // pop component dev
	}

	private SEFFInterpretationProgressed repeat(final SEFFInterpretationContext entity) {
		return new SEFFInterpretationProgressed(entity);
	}

}
