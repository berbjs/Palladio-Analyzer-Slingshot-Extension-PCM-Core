package org.palladiosimulator.analyzer.slingshot.common.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

/**
 * This class provides methods for programmatically saving EMF resource files
 * after editing them.
 *
 * @author Julijan Katic
 *
 */
public final class ResourceUtils {

	private static final Logger LOGGER = Logger.getLogger(ResourceUtils.class);

	public static Resource createAndAddResource(final String outputFile, final String[] fileExtensions,
			final ResourceSet rs) {
		for (final String fileExt : fileExtensions) {
			rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(fileExt, new XMLResourceFactoryImpl());
		}
		final URI uri = URI.createFileURI(outputFile);
		final Resource resource = rs.createResource(uri);
		((ResourceImpl) resource).setIntrinsicIDToEObjectMap(new HashMap<>());
		return resource;
	}

	private static void saveResource(final Resource resource) {
		final Map saveOptions = ((XMLResource) resource).getDefaultSaveOptions();
		saveOptions.put(XMLResource.OPTION_CONFIGURATION_CACHE, Boolean.TRUE);
		saveOptions.put(XMLResource.OPTION_USE_CACHED_LOOKUP_TABLE, new ArrayList<>());

		try {
			resource.save(saveOptions);
		} catch (final IOException e) {
			LOGGER.error(e);
		}
	}

	public static URI insertFragment(final URI uri, final String fragment, final int position) {
		if (position > uri.segmentCount()) {
			throw new IllegalArgumentException("position is out of range.");
		}

		final List<String> seg = new ArrayList<String>(uri.segmentsList());
		seg.add(position, fragment);
		final URI newUri = URI.createHierarchicalURI(uri.scheme(), uri.authority(), uri.device(),
				seg.toArray(new String[0]), uri.query(), uri.fragment());
		return newUri;
	}

	public static void saveAllResource(final Allocation allocation, final String idSegment) {
		if (allocation.getSystem_Allocation().getAssemblyContexts__ComposedStructure().isEmpty()) {
			throw new IllegalArgumentException("Number of AssemblyContexts is zero, abort saving.");
		}

		final ResourceEnvironment resourceEnvironment = allocation.getTargetResourceEnvironment_Allocation();
		final org.palladiosimulator.pcm.system.System system = allocation.getSystem_Allocation();
		final Repository repo = allocation.getSystem_Allocation().getAssemblyContexts__ComposedStructure().get(0)
				.getEncapsulatedComponent__AssemblyContext().getRepository__RepositoryComponent();

		final URI oldAllocUri = allocation.eResource().getURI();
		final URI newAllocUri = ResourceUtils.insertFragment(oldAllocUri, idSegment, oldAllocUri.segmentCount() - 1);
		allocation.eResource().setURI(newAllocUri);

		final URI oldResUri = resourceEnvironment.eResource().getURI();
		final URI newResUri = ResourceUtils.insertFragment(oldResUri, idSegment, oldResUri.segmentCount() - 1);
		resourceEnvironment.eResource().setURI(newResUri);

		final URI oldSysUri = system.eResource().getURI();
		final URI newSysUri = ResourceUtils.insertFragment(oldSysUri, idSegment, oldSysUri.segmentCount() - 1);
		system.eResource().setURI(newSysUri);

		final URI oldRepoUri = repo.eResource().getURI();
		final URI newRepoUri = ResourceUtils.insertFragment(oldRepoUri, idSegment, oldRepoUri.segmentCount() - 1);
		repo.eResource().setURI(newRepoUri);

		ResourceUtils.saveResource(repo.eResource());
		ResourceUtils.saveResource(resourceEnvironment.eResource());
		ResourceUtils.saveResource(system.eResource());
		ResourceUtils.saveResource(allocation.eResource());

		allocation.eResource().setURI(oldAllocUri);
		system.eResource().setURI(oldSysUri);
		resourceEnvironment.eResource().setURI(oldResUri);
		repo.eResource().setURI(oldRepoUri);
	}

	/**
	 *
	 * Save a resource model to the given path. Beware this operation moves the
	 * model to another resource, i.e. changes it!
	 *
	 * The path could be e.g.
	 * "platform:/resource/RemoteMeasuringMosaic/rm_output.resourceenvironment"
	 *
	 * @param resEnv model to be saved.
	 * @param path   location to safe the model at.
	 */
	public static void setupAndSaveResourceModel(final ResourceEnvironment resEnv, final String path) {

		final ResourceSet rs = new ResourceSetImpl();
		final Resource reResource = createResource(path, rs);
		reResource.getContents().add(resEnv);
		saveResource(reResource);

	}

	private static Resource createResource(final String outputFile, final ResourceSet rs) {
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("resourceenvironment",
				new XMLResourceFactoryImpl());
		final URI uri = URI.createURI(outputFile);
		final Resource resource = rs.createResource(uri);
		((ResourceImpl) resource).setIntrinsicIDToEObjectMap(new HashMap<>());
		return resource;
	}
}
