package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserContextEntityHolder;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Signature;

/**
 * This entity is used for having a context for the resource interpreter. It can
 * contain all the necessary information for the interpreter so that the
 * interpretation can be successful. It is, however, important to know that
 * sometimes not all or every information is needed.
 * 
 * @author Julijan Katic
 */
public class RepositoryInterpretationContext extends UserContextEntityHolder {

	/**
	 * A provided role to either be interpreted or to be used as an information to
	 * find an entity
	 */
	private ProvidedRole providedRole;

	/** A signature when it is needed to find a service. */
	private Signature signature;

	/** The assembly context from which to find another entity connected to this. */
	private AssemblyContext assemblyContext;

	/** The list of variable input parameters. */
	private EList<VariableUsage> inputParameters;

	/**
	 * Instantiates the context holder with a user.
	 * 
	 * @param user The user context, typically the context that requested the
	 *             interpretation.
	 */
	public RepositoryInterpretationContext(final User user) {
		super(user);
	}

	public ProvidedRole getProvidedRole() {
		return providedRole;
	}

	public void setProvidedRole(final ProvidedRole providedRole) {
		this.providedRole = providedRole;
	}

	public Signature getSignature() {
		return signature;
	}

	public void setSignature(final Signature signature) {
		this.signature = signature;
	}

	public EList<VariableUsage> getInputParameters() {
		return inputParameters;
	}

	public void setInputParameters(final EList<VariableUsage> variableUsages) {
		this.inputParameters = variableUsages;
	}

	public AssemblyContext getAssemblyContext() {
		return assemblyContext;
	}

	public void setAssemblyContext(final AssemblyContext assemblyContext) {
		this.assemblyContext = assemblyContext;
	}

}
