package com.cisco.yangide.ext.model.editor.util;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;

public class YangDiagramImageProvider extends AbstractImageProvider {
    private static final String PATH_PREFIX = "icons/full/obj16/";

    @Override
    protected void addAvailableImages() {        
        addImageFilePath(IYangImageConstants.IMG_IMPORT_PROPOSAL, PATH_PREFIX + "import_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IYangImageConstants.IMG_TYPE_PROPOSAL, PATH_PREFIX + "type_obj.gif"); //$NON-NLS-1$

        addImageFilePath(IYangImageConstants.IMG_GROUPING_PROPOSAL, PATH_PREFIX + "grouping_obj.gif"); //$NON-NLS-1$        
        addImageFilePath(IYangImageConstants.IMG_CONTAINER_PROPOSAL, PATH_PREFIX + "container_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IYangImageConstants.IMG_LEAF_PROPOSAL, PATH_PREFIX + "leaf_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IYangImageConstants.IMG_MODULE_PROPOSAL, PATH_PREFIX + "module_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IYangImageConstants.IMG_SUBMODULE_PROPOSAL, PATH_PREFIX + "submodule_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IYangImageConstants.IMG_USES_PROPOSAL, PATH_PREFIX + "uses_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IYangImageConstants.IMG_INCLUDE_PROPOSAL, PATH_PREFIX + "include_obj.gif"); //$NON-NLS-1$        
        addImageFilePath(IYangImageConstants.IMG_AUGMENT_PROPOSAL, PATH_PREFIX + "augment_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IYangImageConstants.IMG_DEVIATION_PROPOSAL, PATH_PREFIX + "deviation_obj.gif"); //$NON-NLS-1$        
        addImageFilePath(IYangImageConstants.IMG_EXTENSION_PROPOSAL, PATH_PREFIX + "extension_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IYangImageConstants.IMG_IDENTITY_PROPOSAL, PATH_PREFIX + "identity_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IYangImageConstants.IMG_NOTIFICATION_PROPOSAL, PATH_PREFIX + "notification_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IYangImageConstants.IMG_RPC_INPUT_PROPOSAL, PATH_PREFIX + "rpc_input_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IYangImageConstants.IMG_RPC_OUTPUT_PROPOSAL, PATH_PREFIX + "rpc_output_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IYangImageConstants.IMG_RPC_PROPOSAL, PATH_PREFIX + "rpc_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IYangImageConstants.IMG_ANYXML_PROPOSAL, PATH_PREFIX + "anyxml_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IYangImageConstants.IMG_FEATURE_PROPOSAL, PATH_PREFIX + "feature_obj.gif"); //$NON-NLS-1$
    }

}
