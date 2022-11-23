package org.palladiosimulator.analyzer.slingshot.common.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

	public static URI insertFragment(final URI uri, final String fragment, final int position) {
		if (position > uri.segmentCount()) {
			throw new IllegalArgumentException("position is out of range.");
		}

		List<String> seg = new ArrayList<String>(uri.segmentsList());
		seg.add(position, fragment);
		URI newUri = URI.createHierarchicalURI(uri.scheme(), uri.authority(), uri.device(), seg.toArray(new String[0]), uri.query(), uri.fragment());
		return newUri;
	}

}
