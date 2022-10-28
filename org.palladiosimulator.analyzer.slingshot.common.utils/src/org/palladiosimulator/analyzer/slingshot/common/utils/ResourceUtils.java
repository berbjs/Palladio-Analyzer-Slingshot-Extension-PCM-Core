package org.palladiosimulator.analyzer.slingshot.common.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;

/**
 * This class provides methods for programmatically saving EMF resource files
 * after editing them.
 * 
 * @author Julijan Katic
 *
 */
public final class ResourceUtils {

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

	public static void saveResource(final Resource resource) {
		final Map<Object, Object> saveOptions = ((XMLResource) resource).getDefaultSaveOptions();
		saveOptions.put(XMLResource.OPTION_CONFIGURATION_CACHE, Boolean.TRUE);
		saveOptions.put(XMLResource.OPTION_USE_CACHED_LOOKUP_TABLE, new ArrayList<>());
		try {
			resource.save(saveOptions);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}
