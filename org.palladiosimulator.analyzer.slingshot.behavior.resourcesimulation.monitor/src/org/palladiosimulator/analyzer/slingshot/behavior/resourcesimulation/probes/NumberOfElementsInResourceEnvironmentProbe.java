package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.probes;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ActiveResourceStateUpdated;
import org.palladiosimulator.analyzer.slingshot.behavior.spd.data.ModelAdjusted;
import org.palladiosimulator.analyzer.slingshot.behavior.spd.data.adjustment.ResourceEnvironmentChange;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventBasedListProbe;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

/**
 *
 * @author Sarah Stieß
 *
 */
public final class NumberOfElementsInResourceEnvironmentProbe extends EventBasedListProbe<Long, Dimensionless> {

	/**
	 * Constructs a StateOfActiveResourceProbe.
	 */
	public NumberOfElementsInResourceEnvironmentProbe() {
		super(MetricDescriptionConstants.NUMBER_OF_RESOURCE_CONTAINERS_OVER_TIME);
		// yes, this one subsumes
	}

	@Override
	public Measure<Long, Dimensionless> getMeasurement(final DESEvent event) {
		if (event instanceof ModelAdjusted) {

			final ResourceEnvironmentChange resEnvChange = ((ModelAdjusted) event).getChanges().stream()
					.filter(change -> (change instanceof ResourceEnvironmentChange))
					.map(change -> (ResourceEnvironmentChange) change).findAny().orElseGet(() -> {
						throw new IllegalArgumentException(
								String.format("Expected an ResourceEnvironmentChange, but found none."));
					});

			final int numberOfElements = resEnvChange.getOldResourceContainers().size()
					- resEnvChange.getDeletedResourceContainers().size()
					+ resEnvChange.getNewResourceContainers().size();

			return Measure.valueOf(Long.valueOf(numberOfElements), Dimensionless.UNIT);
		}
		throw new IllegalArgumentException(String.format("Wrong eventype. Expected %s but got %s.",
				ActiveResourceStateUpdated.class.getSimpleName(), event.getClass().getSimpleName()));
	}
}
