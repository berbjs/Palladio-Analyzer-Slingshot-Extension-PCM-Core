package org.palladiosimulator.analyzer.slingshot.behavior.usageevolution;

import org.palladiosimulator.analyzer.slingshot.behavior.usageevolution.ui.UsageEvolutionArchitectureModelTabConfigurer;
import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;
import org.scaledl.usageevolution.UsageEvolution;

public class UsageEvolutionModule extends AbstractSlingshotExtension {

	@Override
	protected void configure() {
		install(UsageEvolutionBehavior.class);
		install(UsageEvolutionArchitectureModelTabConfigurer.class);
		provideModel(UsageEvolution.class, UsageEvolutionModelProvider.class);
	}

	@Override
	public String getName() {
		return UsageEvolutionModule.class.getSimpleName();
	}

}
