/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.ui.internal;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import com.cisco.yangide.ui.YangUIPlugin;

/**
 * The images provided by the external tools plugin.
 *
 * @author Konstantin Zaitsev
 * @date Jul 14, 2014
 */
public class YangUIImages {

    /**
     * The image registry containing <code>Image</code>s.
     */
    private static ImageRegistry imageRegistry;

    /**
     * The registry for composite images
     */
    private static ImageDescriptorRegistry imageDescriptorRegistry;

    private static String ICONS_PATH = "$nl$/icons/full/"; //$NON-NLS-1$

    // Use IPath and toOSString to build the names to ensure they have the slashes correct
    @SuppressWarnings("unused")
    private final static String LOCALTOOL = ICONS_PATH + "elcl16/"; //basic colors - size 16x16 //$NON-NLS-1$
    private final static String OBJECT = ICONS_PATH + "obj16/"; //basic colors - size 16x16 //$NON-NLS-1$
    @SuppressWarnings("unused")
    private final static String OVR = ICONS_PATH + "ovr16/"; //basic colors - size 7x8 //$NON-NLS-1$
    private final static String WIZ = ICONS_PATH + "wizban/"; //$NON-NLS-1$
    @SuppressWarnings("unused")
    private static final String T_ETOOL = ICONS_PATH + "etool16"; //$NON-NLS-1$

    /**
     * Declare all images
     */
    private static void declareImages() {
        // Editor images
        declareRegistryImage(IYangUIConstants.IMG_TEMPLATE_PROPOSAL, OBJECT + "template_obj.gif"); //$NON-NLS-1$
        declareRegistryImage(IYangUIConstants.IMG_IMPORT_PROPOSAL, OBJECT + "import_obj.gif"); //$NON-NLS-1$
        declareRegistryImage(IYangUIConstants.IMG_KEYWORD_PROPOSAL, OBJECT + "keyword_obj.gif"); //$NON-NLS-1$
        declareRegistryImage(IYangUIConstants.IMG_TYPE_PROPOSAL, OBJECT + "type_obj.gif"); //$NON-NLS-1$
        declareRegistryImage(IYangUIConstants.IMG_CUSTOM_TYPE_PROPOSAL, OBJECT + "custom_type_obj.gif"); //$NON-NLS-1$
        declareRegistryImage(IYangUIConstants.IMG_GROUPING_PROPOSAL, OBJECT + "grouping_obj.gif"); //$NON-NLS-1$        
        declareRegistryImage(IYangUIConstants.IMG_CONTAINER_PROPOSAL, OBJECT + "container_obj.gif"); //$NON-NLS-1$
        declareRegistryImage(IYangUIConstants.IMG_LEAF_PROPOSAL, OBJECT + "leaf_obj.gif"); //$NON-NLS-1$
        declareRegistryImage(IYangUIConstants.IMG_MODULE_PROPOSAL, OBJECT + "module_obj.gif"); //$NON-NLS-1$
        declareRegistryImage(IYangUIConstants.IMG_SUBMODULE_PROPOSAL, OBJECT + "submodule_obj.gif"); //$NON-NLS-1$
        declareRegistryImage(IYangUIConstants.IMG_USES_PROPOSAL, OBJECT + "uses_obj.gif"); //$NON-NLS-1$
        declareRegistryImage(IYangUIConstants.IMG_INCLUDE_PROPOSAL, OBJECT + "include_obj.gif"); //$NON-NLS-1$

        // Marker images
        declareRegistryImage(IYangUIConstants.IMG_ERROR_MARKER, OBJECT + "error_obj.gif"); //$NON-NLS-1$
        declareRegistryImage(IYangUIConstants.IMG_ERROR_MARKER_ALT, OBJECT + "error_alt_obj.gif"); //$NON-NLS-1$

        // Wizard images
        declareRegistryImage(IYangUIConstants.IMG_NEW_PROJECT_WIZ, WIZ + "newprj_wiz.png"); //$NON-NLS-1$
        declareRegistryImage(IYangUIConstants.IMG_NEW_FILE_WIZ, WIZ + "newfile_wiz.png"); //$NON-NLS-1$
    }

    /**
     * Declare an Image in the registry table.
     *
     * @param key The key to use when registering the image
     * @param path The path where the image can be found. This path is relative to where this plugin
     * class is found (i.e. typically the packages directory)
     */
    private final static void declareRegistryImage(String key, String path) {
        ImageDescriptor desc = ImageDescriptor.getMissingImageDescriptor();
        Bundle bundle = Platform.getBundle(YangUIPlugin.PLUGIN_ID);
        URL url = null;
        if (bundle != null) {
            url = FileLocator.find(bundle, new Path(path), null);
            desc = ImageDescriptor.createFromURL(url);
        }
        imageRegistry.put(key, desc);
    }

    /**
     * Returns the ImageRegistry.
     */
    public static ImageRegistry getImageRegistry() {
        if (imageRegistry == null) {
            initializeImageRegistry();
        }
        return imageRegistry;
    }

    /**
     * Initialize the image registry by declaring all of the required graphics. This involves
     * creating JFace image descriptors describing how to create/find the image should it be needed.
     * The image is not actually allocated until requested. Prefix conventions Wizard Banners
     * WIZBAN_ Preference Banners PREF_BAN_ Property Page Banners PROPBAN_ Color toolbar CTOOL_
     * Enable toolbar ETOOL_ Disable toolbar DTOOL_ Local enabled toolbar ELCL_ Local Disable
     * toolbar DLCL_ Object large OBJL_ Object small OBJS_ View VIEW_ Product images PROD_ Misc
     * images MISC_ Where are the images? The images (typically gifs) are found in the same location
     * as this plugin class. This may mean the same package directory as the package holding this
     * class. The images are declared using this.getClass() to ensure they are looked up via this
     * plugin class.
     *
     * @see org.eclipse.jface.resource.ImageRegistry
     */
    public static ImageRegistry initializeImageRegistry() {
        imageRegistry = new ImageRegistry(YangUIPlugin.getStandardDisplay());
        declareImages();
        return imageRegistry;
    }

    /**
     * Returns the <code>Image<code> identified by the given key,
     * or <code>null</code> if it does not exist.
     */
    public static Image getImage(String key) {
        return getImageRegistry().get(key);
    }

    /**
     * Returns the <code>ImageDescriptor<code> identified by the given key,
     * or <code>null</code> if it does not exist.
     */
    public static ImageDescriptor getImageDescriptor(String key) {
        return getImageRegistry().getDescriptor(key);
    }

    /**
     * Returns the image for the given composite descriptor.
     */
    public synchronized static Image getImage(CompositeImageDescriptor imageDescriptor) {
        if (imageDescriptorRegistry == null) {
            imageDescriptorRegistry = new ImageDescriptorRegistry();
        }
        return imageDescriptorRegistry.get(imageDescriptor);
    }

    public static void disposeImageDescriptorRegistry() {
        if (imageDescriptorRegistry != null) {
            imageDescriptorRegistry.dispose();
        }
    }

    /**
     * Returns whether the images have been initialized.
     *
     * @return whether the images have been initialized
     */
    public synchronized static boolean isInitialized() {
        return imageDescriptorRegistry != null;
    }
}
