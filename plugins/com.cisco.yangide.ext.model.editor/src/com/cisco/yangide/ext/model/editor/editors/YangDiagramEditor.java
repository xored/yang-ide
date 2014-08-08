package com.cisco.yangide.ext.model.editor.editors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;
import org.eclipse.ui.IEditorInput;

import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.editor.util.DiagramImportSupport;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class YangDiagramEditor extends DiagramEditor {

    @Override
    protected DiagramBehavior createDiagramBehavior() {
        return new YangDiagramBehavior(this);
    }

    @Override
    protected void setInput(IEditorInput input) {
        super.setInput(input);
        final URI uri = ((IDiagramEditorInput) input).getUri();
        final Module m = ((YangDiagramEditorInput) input).getModule();
        getEditingDomain().getCommandStack().execute(new RecordingCommand(getEditingDomain()) {

            @Override
            protected void doExecute() {
                ensureDiagramResource(uri);
                importDiagram(m);
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

    private void importDiagram(Module m) {
        if (null != m && YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getModule(), m)) {
            final Diagram diagram = getDiagramTypeProvider().getDiagram();
            getDiagramTypeProvider().getFeatureProvider().link(diagram, m);
            DiagramImportSupport.importDiagram(diagram, getDiagramTypeProvider().getFeatureProvider());
        }

    }
}
