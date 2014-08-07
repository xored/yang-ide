package com.cisco.yangide.ext.model.editor.editors;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;
import org.eclipse.jdt.internal.core.JarEntryFile;
import org.eclipse.jdt.internal.ui.javaeditor.JarEntryEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangJarFileEntryResource;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.editor.util.DiagramImportSupport;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

@SuppressWarnings("restriction")
public class YangDiagramEditor extends DiagramEditor {

    @Override
    protected DiagramBehavior createDiagramBehavior() {
        return new YangDiagramBehavior(this);
    }

    @Override
    protected void setInput(IEditorInput input) {
        super.setInput(input);
        final URI uri = ((IDiagramEditorInput) input).getUri();
        getEditingDomain().getCommandStack().execute(new RecordingCommand(getEditingDomain()) {

            @Override
            protected void doExecute() {
                ensureDiagramResource(uri);
                importDiagram(uri);
            }
        });
    }
    private void ensureDiagramResource(URI uri) {
        final Resource resource = getEditingDomain().getResourceSet().createResource(uri);
        getEditingDomain().getCommandStack().execute(new RecordingCommand(getEditingDomain()) {
            @Override
            protected void doExecute() {
                resource.setTrackingModification(true);
                resource.getContents().add(getDiagramTypeProvider().getDiagram());
            }
        });
    }
    private void importDiagram(URI uri) {        
        Module m = YangModelUtil.exportModel(getModule());
        if (null != m && YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getModule(), m)) {
            final Diagram diagram = getDiagramTypeProvider().getDiagram();         
            getDiagramTypeProvider().getFeatureProvider().link(diagram, m);
            DiagramImportSupport.importDiagram(diagram, getDiagramTypeProvider().getFeatureProvider());
        }

    }
 
    private com.cisco.yangide.core.dom.Module getModule() {
        IEditorInput input = getEditorInput();
        try {
            if (input instanceof IFileEditorInput) {
                IFile file = ((IFileEditorInput) input).getFile();

                return YangCorePlugin.createYangFile(file).getModule();

            } else if (input instanceof IDiagramEditorInput) {
                URI uri = ((IDiagramEditorInput) input).getUri();
                if (uri.isFile()) {
                    IFile file = YangCorePlugin.getIFileFromFile(new File(((IDiagramEditorInput) input).getUri().toFileString()));
                    return YangCorePlugin.createYangFile(file).getModule();
                } else if (uri.isPlatformResource()) {
                    IFile file = ResourcesPlugin.getWorkspace().getRoot()
                            .getFile(new Path(uri.toPlatformString(true)));
                    return YangCorePlugin.createYangFile(file).getModule();
                }
                
            } else if (input instanceof JarEntryEditorInput) {
                JarEntryEditorInput jarInput = (JarEntryEditorInput) input;
                IStorage storage = jarInput.getStorage();
                if (storage instanceof YangJarFileEntryResource) {
                    YangJarFileEntryResource jarEntry = (YangJarFileEntryResource) storage;
                    return YangCorePlugin.createJarEntry(jarEntry.getPath(), jarEntry.getEntry()).getModule();
                } else if (storage instanceof JarEntryFile) {
                    JarEntryFile jarEntry = (JarEntryFile) storage;
                    return YangCorePlugin.createJarEntry(jarEntry.getPackageFragmentRoot().getPath(),
                            jarEntry.getFullPath().makeRelative().toString()).getModule();
                }
            }
        } catch (YangModelException e) {
            e.printStackTrace();
        }
        return null;
    }

}
