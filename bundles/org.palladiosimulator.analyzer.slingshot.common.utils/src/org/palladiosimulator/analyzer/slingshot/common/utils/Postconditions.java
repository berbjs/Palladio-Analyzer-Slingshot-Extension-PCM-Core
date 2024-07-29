package org.palladiosimulator.analyzer.slingshot.common.utils;

import java.util.List;
import java.util.Set;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;

import com.google.common.base.Preconditions;

/**
 * This class provides static methods for analyzing postconditions, especially
 * for determining that only certain events are given.
 * 
 * @author Julijan Katic
 * @version 1.0
 */
public class Postconditions {

	/**
	 * Checks that beside type compatability, the set has a certain maximum size. The type compatability is checked by
	 * {@link Postconditions#checkResultEventTypes}.
	 * 
	 * @param resultEventSet The set of concrete event instances. Must neither be null nor empty.
	 * @param types The list of types against which the result set will be checked. Must neither be null nor empty.
	 * @param maximalSize The maximal size that the set should have.
	 * @return true if the set is type-compatible and has the maximum size specified.
	 * @see #checkResultEventTypes
	 */
	public static boolean checkResultEventTypesAndSize(final Set<DESEvent> resultEventSet, final List<Class<? extends DESEvent>> types, final int maximalSize) {
		Preconditions.checkArgument(maximalSize >= 0);
		return checkResultEventTypes(resultEventSet, types) && resultEventSet.size() <= maximalSize;
	}
	
	/**
	 * Checks whether the events in the result set is represented in the types list. This is useful to check whether the
	 * result set only contains events of certain types, and hence can be made for assertions. Returns true if this is the
	 * case.
	 * 
	 * @param resultEventSet The set of concrete event instances. Must neither be null nor empty.
	 * @param types The list of types against which the result set will be checked. Must neither be null nor empty.
	 * @return true if for each event its type is contained in the list.
	 */
	public static boolean checkResultEventTypes(final Set<DESEvent> resultEventSet, final List<Class<? extends DESEvent>> types) {
		Preconditions.checkArgument(resultEventSet != null && !resultEventSet.isEmpty());
		Preconditions.checkArgument(types != null && !types.isEmpty());
		return resultEventSet.stream().allMatch(event -> types.contains(event.getClass()));
	}
}
