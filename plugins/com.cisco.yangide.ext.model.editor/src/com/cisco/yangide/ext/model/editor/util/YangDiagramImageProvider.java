package com.cisco.yangide.ext.model.editor.util;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;

public class YangDiagramImageProvider extends AbstractImageProvider {
    
    public static final String PLUGIN_ID = "com.cisco.yangide.ext.model.editor";
    public static final String DIAGRAM_TYPE_PROVIDER_ID = "com.cisco.yangide.ext.model.editor.editorDiagramTypeProvider";
    
    //public static final String IMG_TEMPLATE_PROPOSAL = PLUGIN_ID + ".template_obj"; //$NON-NLS-1$
    public static final String IMG_IMPORT_PROPOSAL = PLUGIN_ID + ".import_obj";
    //public static final String IMG_KEYWORD_PROPOSAL = PLUGIN_ID + ".keyword_obj";
    public static final String IMG_TYPE_PROPOSAL = PLUGIN_ID + ".type_obj";
    //public static final String IMG_CUSTOM_TYPE_PROPOSAL = PLUGIN_ID + ".custom_type_obj";
    public static final String IMG_GROUPING_PROPOSAL = PLUGIN_ID + ".grouping_obj";
    public static final String IMG_CONTAINER_PROPOSAL = PLUGIN_ID + ".container_obj";
    public static final String IMG_LEAF_PROPOSAL = PLUGIN_ID + ".leaf_obj";
    public static final String IMG_LEAF_LIST_PROPOSAL = PLUGIN_ID + ".leaf_list_obj";
    public static final String IMG_LIST_PROPOSAL = PLUGIN_ID + ".list_obj";
    public static final String IMG_CHOICE_PROPOSAL = PLUGIN_ID + ".choice_obj";
    public static final String IMG_CHOICE_CASE_PROPOSAL = PLUGIN_ID + ".choice_case_obj";
    public static final String IMG_MODULE_PROPOSAL = PLUGIN_ID + ".module_obj";
    public static final String IMG_SUBMODULE_PROPOSAL = PLUGIN_ID + ".submodule_obj";
    public static final String IMG_USES_PROPOSAL = PLUGIN_ID + ".uses_obj";
    public static final String IMG_INCLUDE_PROPOSAL = PLUGIN_ID + ".include_obj";    
    public static final String IMG_AUGMENT_PROPOSAL = PLUGIN_ID + ".augment_obj";
    public static final String IMG_DEVIATION_PROPOSAL = PLUGIN_ID + ".deviation_obj";
    public static final String IMG_EXTENSION_PROPOSAL = PLUGIN_ID + ".extension_obj";
    public static final String IMG_IDENTITY_PROPOSAL = PLUGIN_ID + ".identity_obj";
    public static final String IMG_NOTIFICATION_PROPOSAL = PLUGIN_ID + ".notification_obj";
    public static final String IMG_RPC_INPUT_PROPOSAL = PLUGIN_ID + ".rpc_input_obj";
    public static final String IMG_RPC_OUTPUT_PROPOSAL = PLUGIN_ID + ".rpc_output_obj";
    public static final String IMG_RPC_PROPOSAL = PLUGIN_ID + ".rpc_obj";
    public static final String IMG_ANYXML_PROPOSAL = PLUGIN_ID + ".anyxml_obj";
    public static final String IMG_FEATURE_PROPOSAL = PLUGIN_ID + ".feature_obj";
    
    public static final String IMG_ADD_TOOL_PROPOSAL = PLUGIN_ID + ".add_tool_obj";
    public static final String IMG_DELETE_TOOL_PROPOSAL = PLUGIN_ID + ".delete_tool_obj";
    public static final String IMG_COLLAPSE_ALL_TOOL_PROPOSAL = PLUGIN_ID + ".collapseall_tool_obj";
    
    
    private static final String PATH_OBJ = "icons/full/obj16/";
    private static final String PATH_TOOL = "icons/full/etool16/";

    @Override
    protected void addAvailableImages() {        
        addImageFilePath(IMG_IMPORT_PROPOSAL, PATH_OBJ + "import_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_TYPE_PROPOSAL, PATH_OBJ + "type_obj.gif"); //$NON-NLS-1$

        addImageFilePath(IMG_GROUPING_PROPOSAL, PATH_OBJ + "grouping_obj.gif"); //$NON-NLS-1$        
        addImageFilePath(IMG_CONTAINER_PROPOSAL, PATH_OBJ + "container_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_LEAF_PROPOSAL, PATH_OBJ + "leaf_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_LEAF_LIST_PROPOSAL, PATH_OBJ + "leaf_list_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_LIST_PROPOSAL, PATH_OBJ + "list_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_CHOICE_PROPOSAL, PATH_OBJ + "choice_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_CHOICE_CASE_PROPOSAL, PATH_OBJ + "choice_case_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_MODULE_PROPOSAL, PATH_OBJ + "module_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_SUBMODULE_PROPOSAL, PATH_OBJ + "submodule_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_USES_PROPOSAL, PATH_OBJ + "uses_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_INCLUDE_PROPOSAL, PATH_OBJ + "include_obj.gif"); //$NON-NLS-1$        
        addImageFilePath(IMG_AUGMENT_PROPOSAL, PATH_OBJ + "augment_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_DEVIATION_PROPOSAL, PATH_OBJ + "deviation_obj.gif"); //$NON-NLS-1$        
        addImageFilePath(IMG_EXTENSION_PROPOSAL, PATH_OBJ + "extension_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_IDENTITY_PROPOSAL, PATH_OBJ + "identity_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_NOTIFICATION_PROPOSAL, PATH_OBJ + "notification_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_RPC_INPUT_PROPOSAL, PATH_OBJ + "rpc_input_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_RPC_OUTPUT_PROPOSAL, PATH_OBJ + "rpc_output_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_RPC_PROPOSAL, PATH_OBJ + "rpc_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_ANYXML_PROPOSAL, PATH_OBJ + "anyxml_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_FEATURE_PROPOSAL, PATH_OBJ + "feature_obj.gif"); //$NON-NLS-1$
        
        addImageFilePath(IMG_ADD_TOOL_PROPOSAL, PATH_TOOL + "add_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_DELETE_TOOL_PROPOSAL, PATH_TOOL + "delete_obj.gif"); //$NON-NLS-1$
        addImageFilePath(IMG_COLLAPSE_ALL_TOOL_PROPOSAL, PATH_TOOL + "collapseall_obj.gif"); //$NON-NLS-1$
    }

}
