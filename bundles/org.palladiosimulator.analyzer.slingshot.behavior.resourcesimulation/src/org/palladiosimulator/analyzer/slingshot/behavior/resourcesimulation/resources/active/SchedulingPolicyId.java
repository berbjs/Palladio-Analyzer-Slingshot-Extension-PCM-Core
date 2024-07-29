package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active;

import org.palladiosimulator.pcm.resourcetype.SchedulingPolicy;

/**
 * The scheduling policy id is the id of a {@link SchedulingPolicy}. In PCM, the
 * {@code SchedulingPolicy} itself is not an enumerator anymore. This enum is
 * just for convenience in order to define the right constants.
 * 
 * @author Julijan Katic
 *
 */
public enum SchedulingPolicyId {
	/** The FCFS resource with id "FCFS" */
	FCFS("FCFS"),

	/** Processor Sharing with id "ProcessorSharing" */
	PROCESSOR_SHARING("ProcessorSharing"),

	/** The delay resource with id "Delay" */
	DELAY("Delay"),

	/**
	 * The default resource with an empty id. Should be used as the default case.
	 */
	DEFAULT("");

	/** The id of the resource. */
	private final String id;

	private SchedulingPolicyId(final String id) {
		this.id = id;
	}

	/**
	 * Returns the id of the resource.
	 * 
	 * @return
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Constructs the appropriate enum constant from the {@link SchedulingPolicy}.
	 * If the scheduling policy is not known, {@link #DEFAULT} will be returned.
	 * 
	 * @param policy The policy from which the id should be extracted.
	 * @return the id of the policy if known, otherwise {@link #DEFAULT}.
	 */
	public static SchedulingPolicyId retrieveFromSchedulingPolicy(final SchedulingPolicy policy) {
		final String id = policy.getId();
		SchedulingPolicyId schedulingPolicyId = null;

		for (final SchedulingPolicyId policyId : SchedulingPolicyId.values()) {
			if (id.equals(policyId.id)) {
				schedulingPolicyId = policyId;
				break;
			}
		}

		return schedulingPolicyId == null ? SchedulingPolicyId.DEFAULT : schedulingPolicyId;
	}

}
