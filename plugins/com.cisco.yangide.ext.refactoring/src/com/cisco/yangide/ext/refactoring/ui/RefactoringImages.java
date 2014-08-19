/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.refactoring.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import com.cisco.yangide.ext.refactoring.YangRefactoringPlugin;
import com.cisco.yangide.ui.YangUIPlugin;

/**
 * @author Konstantin Zaitsev
 * @date Aug 18, 2014
 */
public final class RefactoringImages {
    public static final String IMG_CALENDAR = "calendar"; //$NON-NLS-1$

    private static ImageRegistry registry;

    /**
     * Declare all images
     */
    private static void declareImages() {
        declareRegistryImage(IMG_CALENDAR, "icons/calendar.gif"); //$NON-NLS-1$
    }

    private final static void declareRegistryImage(String key, String path) {
        ImageDescriptor desc = ImageDescriptor.getMissingImageDescriptor();
        Bundle bundle = Platform.getBundle(YangRefactoringPlugin.PLUGIN_ID);
        URL url = null;
        if (bundle != null) {
            url = FileLocator.find(bundle, new Path(path), null);
            desc = ImageDescriptor.createFromURL(url);
        }
        registry.put(key, desc);
    }

    /**
     * Returns the ImageRegistry.
     */
    public static ImageRegistry getImageRegistry() {
        if (registry == null) {
            registry = new ImageRegistry(YangUIPlugin.getStandardDisplay());
            declareImages();
        }
        return registry;
    }

    public static Image getImage(String key) {
        return getImageRegistry().get(key);
    }
}
