package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.InterArrivalTime;

/**
 * This class is a context for open workload users. Open workload contexts have
 * an {@link InterArrivalTime} which creates a new user after that time. This
 * differs from closed workload users
 * ({@link ClosedWorkloadUserInterpretationContext}) that let user re-enter the
 * system after a time.
 * 
 * @author Julijan Katic
 * @version 1.0
 */
public final class OpenWorkloadUserInterpretationContext extends UserInterpretationContext {

	/** The interArrivalTime to use. */
	private final InterArrivalTime interArrivalTime;

	public OpenWorkloadUserInterpretationContext(final Builder builder) {
		super(builder);
		this.interArrivalTime = builder.getInterArrivalTime();
	}

	/**
	 * Returns the {@link InterArrivalTime} proxy class that lets enter a new user
	 * after that time independently of older users.
	 * 
	 * @return non-{@code null} instances of that proxy class.
	 */
	public InterArrivalTime getInterArrivalTime() {
		return this.interArrivalTime;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Builder update() {
		return this.updateWithBuilder(builder());
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder extends BaseBuilder<OpenWorkloadUserInterpretationContext, Builder> {

		private InterArrivalTime interArrivalTime;

		public Builder withInterArrivalTime(final InterArrivalTime interArrivalTime) {
			this.interArrivalTime = interArrivalTime;
			return this;
		}

		public InterArrivalTime getInterArrivalTime() {
			return this.interArrivalTime;
		}

		@Override
		public OpenWorkloadUserInterpretationContext build() {
			return new OpenWorkloadUserInterpretationContext(this);
		}

	}

}
