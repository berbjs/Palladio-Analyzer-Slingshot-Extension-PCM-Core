package org.palladiosimulator.analyzer.slingshot.common.utils;

/**
 * Utility class that contains helper methods for certain logical expressions,
 * such as "A ==> B" or "A <==> B". These checks are typically cumbersome to
 * write or read.
 * 
 * @author Julijan Katic
 */
public final class Logic {

	/**
	 * Returns whether the "implies" relationship between the two booleans holds.
	 * The antecedent {@code A} <em>implies</em> the consequent {@code B} (Noted as
	 * {@code A ==> B}) if (NOT {@code A}) OR {@code B}.
	 * 
	 * @param antecedent the boolean from which is being implied.
	 * @param consequent the boolean to which is implied.
	 * @return true if the antecedent implies the consequent.
	 */
	public static boolean implies(final boolean antecedent, final boolean consequent) {
		return !antecedent || consequent;
	}

	/**
	 * Returns whether two booleans are equivalent. Two booleans are considered
	 * equivalent if the truth value of {@code first} is equal to the truth value of
	 * {@code second}. In mathematical sense, {@code A <==> B} if and only if
	 * {@code A ==> B} and {@code B ==> A}. This means that if this method returns
	 * true, then both {@code implies(first, second)} and
	 * {@code implies(second, first)} will result in true.
	 * 
	 * @param first  The first boolean of the equivalence relationship.
	 * @param second the second boolean of the equivalence relationship.
	 * @return true iff both have the same truth value.
	 */
	public static boolean iff(final boolean first, final boolean second) {
		return first == second;
	}
}
