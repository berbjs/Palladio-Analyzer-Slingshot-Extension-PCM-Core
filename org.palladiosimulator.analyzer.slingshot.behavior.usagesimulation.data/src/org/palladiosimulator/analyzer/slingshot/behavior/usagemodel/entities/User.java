package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities;

import java.util.UUID;


import de.uka.ipd.sdq.simucomframework.variables.StackContext;

/**
 * An entity that represents a user within the usage model. In this simulator,
 * each user has an own simulation stack for variables.
 * <p>
 * Two users are considered the same if they both have the same id.
 * 
 * @author Julijan Katic
 */
public class User extends StackContext {

	/**
	 * @generated 
	 */
	private static final long serialVersionUID = 1089208332274419571L;
	
	/** The id of the user. */
	private final String id;

	/**
	 * Constructs the user by creating a new SimulatedStack with a random id and a
	 * new simulated stack.
	 */
	public User() {
		super();
		this.id = UUID.randomUUID().toString();
	}

	/**
	 * Returns the id of this user.
	 * 
	 * @return the id.
	 */
	public String getId() {
		return this.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final User other = (User) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + this.id + "]";
	}

}
