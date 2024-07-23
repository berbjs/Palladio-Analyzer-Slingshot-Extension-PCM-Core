package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities;

import com.google.common.base.Preconditions;

/**
 * A user context entity holder is a class that holds a user instance. This
 * instance is particularly important if certain calculations need certain
 * contexts, like the stack frame where the variables are placed.
 * 
 * @author Julijan Katic
 */
public abstract class UserContextEntityHolder {

	/** The non-null user reference. */
	private final User user;

	/**
	 * Instantiates the entity that holds a non-null user reference.
	 * 
	 * @param user The non-null reference to the user.
	 * @throws NullPointerException if user is null.
	 */
	public UserContextEntityHolder(final User user) {
		this.user = Preconditions.checkNotNull(user, "The user reference must not be null.");
	}

	/**
	 * Returns the user of this context holder.
	 * 
	 * @return the user.
	 */
	public User getUser() {
		return user;
	}
}
