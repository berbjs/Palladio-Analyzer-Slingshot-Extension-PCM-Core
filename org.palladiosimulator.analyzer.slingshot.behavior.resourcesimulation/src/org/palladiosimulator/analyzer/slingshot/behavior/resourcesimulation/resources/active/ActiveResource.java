package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active;

import java.util.Optional;
import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.IResource;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.AbstractJobEvent;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;

/**
 * The active resource provides delegation methods of events, hence it "listens"
 * to events and returns the set of appropriate events. Every active resource
 * need to listen to the {@link JobInitiated} event and {@link JobProgressed}
 * event.
 *
 * @author Julijan Katic
 */
public interface ActiveResource extends IResource {

	/**
	 * Handles the {@link JobInitiated} event. Typically, this results in the
	 * appropriate events for the specific resource, but is often
	 * {@link JobProgressed}.
	 *
	 * @param jobInitiated The event.
	 * @return the appropriate events for the active resource.
	 */
	Optional<AbstractJobEvent> onJobInitiated(final JobInitiated jobInitiated);

	/**
	 * Handles the {@link JobProgressed} event. Typically, this can result in either
	 * a {@link JobProgressed} and/or a {@link JobFinished} event.
	 *
	 * @param jobProgressed The event.
	 * @return the appropriate events for the active resource.
	 */
	Set<AbstractJobEvent> onJobProgressed(final JobProgressed jobProgressed);

}
