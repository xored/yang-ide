/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
package com.cisco.yangide.ext.model.editor.dialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.SimpleNode;
import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.ext.model.Import;
import com.cisco.yangide.ext.model.ModelFactory;
import com.cisco.yangide.ext.model.ModelPackage;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.Node;
import com.cisco.yangide.ext.model.editor.Activator;
import com.cisco.yangide.ext.model.editor.util.Strings;
import com.cisco.yangide.ext.model.editor.util.YangDiagramImageProvider;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

/**
 * This class is a dialog in which user can choose a module to import and its prefix. <br>
 * The existing modules are shown in a list, while the prefix is given by a text box. The first
 * module in the list is chosen by default. When user changes selection in the list, the dialog
 * automatically sets the prefix field to the value defined in the selected module. Value of prefix
 * is examined against the empty value and prefixes already present in the edited module. <br>
 * The resulting {@link Import} object may be obtained from {@link #getResultImport()} if dialog was
 * closed with OK button.
 * 
 * @author Kirill Karmakulov
 * @date 09 Oct 2014
 */
public class AddImportDialog extends ElementListSelectionDialog {

    private Text prefix;
    private ModifyListener prefixModifyListener;

    // Prefixes that are already used by the edited module
    private final Set<String> importPrefixes;

    // Keeps track of parent's Status
    private IStatus fLastStatus;

    private Import result;

    /**
     * Creates a new {@link AddImportDialog}
     * 
     * @param parent the parent shell
     * @param module the edited {@link Module}
     * @param file file to get an {@link IProject} from; the existing modules are taken from this
     * project
     */
    public AddImportDialog(Shell parent, Module module, IFile file) {
        super(parent, new ModuleLabelProvider());
        setAllowDuplicates(false);
        List<Import> imports = getImports(module);
        importPrefixes = getImportData(imports, ModelPackage.Literals.IMPORT__PREFIX);
        setElements(getModuleList(module, file, imports));
        setTitle("Select imported module");
        setImage(GraphitiUi.getImageService().getImageForId(YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID,
                YangDiagramImageProvider.IMG_IMPORT_PROPOSAL));
    }

    /**
     * Creates an area and a text field to enter prefix of the imported module into
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(content);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(content);
        super.createDialogArea(content);
        createPrefixArea(content);
        return content;
    }

    private void createPrefixArea(Composite content) {
        Composite appendix = new Composite(content, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.numColumns = 2;
        appendix.setLayout(layout);
        appendix.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        CLabel label = new CLabel(appendix, SWT.NONE);
        label.setText("Prefix");
        prefix = new Text(appendix, SWT.BORDER);
        prefixModifyListener = new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                updateStatus(getOverallStatus());
            }
        };
        prefix.addModifyListener(prefixModifyListener);
        prefix.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * If prefix field contains an invalid value, returns an error {@link IStatus}. Otherwise
     * returns parent's status.
     * 
     * @return an {@link IStatus} object
     */
    private IStatus getOverallStatus() {
        String value = prefix.getText();
        IStatus status;
        if (value.isEmpty()) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                    String.format("Empty prefix is not allowed", value));
        } else if (importPrefixes.contains(value)) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                    String.format("Prefix \"%s\" is already used", value), null);
        } else
            status = fLastStatus;
        return status;
    }

    /**
     * Saves parent's status internally for use by {@link #getOverallStatus()}.
     */
    @Override
    protected void updateStatus(IStatus status) {
        if (!Activator.PLUGIN_ID.equals(status.getPlugin()))
            // The Status wasn't generated by this class
            fLastStatus = status;
        super.updateStatus(status);
    }

    /**
     * Updates prefix field from the module selected by user. Then updates status of the window if
     * prefix is invalid.
     */
    @Override
    protected void handleSelectionChanged() {
        setDefaultPrefix();
        super.handleSelectionChanged();
        IStatus status = getOverallStatus();
        if (status.getSeverity() != IStatus.OK)
            updateStatus(status);
    }

    /**
     * Updates prefix field from the module selected by user. If the prefix is invalid, fails the
     * default selection and update status of the window. Otherwise behaves just lake parent.
     */
    @Override
    protected void handleDefaultSelected() {
        setDefaultPrefix();
        IStatus status = getOverallStatus();
        if (status.getSeverity() == IStatus.OK)
            super.handleDefaultSelected();
    }

    private void setDefaultPrefix() {
        String defaultPrefix = Strings.EMPTY_STRING;
        Object[] selectedElements = getSelectedElements();
        if (selectedElements.length > 0) // it's a single selection list dialog
        {
            ElementIndexInfo info = (ElementIndexInfo) selectedElements[0];
            try {
                com.cisco.yangide.core.dom.Module importedModule = YangCorePlugin.createYangFile(info.getPath())
                        .getModule();
                SimpleNode<String> prefixNode = importedModule.getPrefix();
                if (prefixNode != null)
                    defaultPrefix = prefixNode.getValue();
            } catch (YangModelException ex) {
                Activator.log(ex, "Yang source file could not be loaded.");
            }
        }
        // There's no need in checking the default prefix, since this is done by hand in
        // handleSelectionChanged() and handleDefaultSelected()
        prefix.removeModifyListener(prefixModifyListener);
        prefix.setText(defaultPrefix);
        prefix.addModifyListener(prefixModifyListener);
    }

    /**
     * Creates the resulting {@link Import} object returned by {@link #getResultImport()}
     */
    @Override
    protected void computeResult() {
        super.computeResult();
        ElementIndexInfo choosen = (ElementIndexInfo) getFirstResult();
        result = ModelFactory.eINSTANCE.createImport();
        result.setPrefix(prefix.getText());
        result.setRevisionDate(choosen.getRevision());
        result.setModule(choosen.getModule());
    }

    public Import getResultImport() {
        return result;
    }

    /**
     * Returns a list of {@link Import} objects defined by the given {@code module}
     */
    private static List<Import> getImports(Module module) {
        List<Import> result = new ArrayList<Import>();
        for (Node _import : YangModelUtil.filter(module.getChildren(), ModelPackage.Literals.IMPORT))
            result.add((Import) _import);
        return result;
    }

    /**
     * Iterates over {@code imports}, and from each object gets a value of the field defined by
     * {@code feature}. The values are put in the resulting {@link Set}.
     * 
     * @param imports source of objects
     * @param feature field of object
     * @return a {@link Set}
     */
    @SuppressWarnings("unchecked")
    private static <T> Set<T> getImportData(List<Import> imports, EStructuralFeature feature) {
        Set<T> result = new HashSet<T>(imports.size());
        for (Import _import : imports)
            result.add((T) ((Import) _import).eGet(feature));
        return result;
    }

    /**
     * Builds a list of modules that the current module can import. <br>
     * Retrieves yang modules that exist in the current project. Then filters out the {@code module}
     * itself and the modules that are already imported. <br>
     * Current project is determined from to the given {@code file}
     * 
     * @param file a file from the current project
     * @param imports a collection of imports of the module
     * @return List of modules that
     */
    private static ElementIndexInfo[] getModuleList(Module module, IFile file, List<Import> imports) {
        ElementIndexInfo[] allModules = YangModelManager.search(null, null, null, ElementIndexType.MODULE,
                null == file ? null : file.getProject(), null);
        List<ElementIndexInfo> result = new ArrayList<ElementIndexInfo>(allModules.length - 1);

        Set<String> importSet = getImportData(imports, ModelPackage.Literals.IMPORT__MODULE);
        String name = module.getName();
        for (ElementIndexInfo info : allModules) {
            String moduleName = info.getModule();
            if (Objects.equals(name, moduleName))
                continue;
            if (importSet.contains(moduleName))
                continue;
            result.add(info);
        }
        return result.toArray(new ElementIndexInfo[result.size()]);
    }

    /**
     * Label provider for {@link Module}
     */
    private static final class ModuleLabelProvider extends LabelProvider {
        public String getText(Object element) {
            if (element instanceof ElementIndexInfo) {
                return ((ElementIndexInfo) element).getName() + " {" + ((ElementIndexInfo) element).getRevision() + "}";
            }
            return null;
        }

        @Override
        public Image getImage(Object element) {
            return GraphitiUi.getImageService().getImageForId(YangDiagramImageProvider.DIAGRAM_TYPE_PROVIDER_ID,
                    YangDiagramImageProvider.IMG_MODULE_PROPOSAL);
        }
    }

}
