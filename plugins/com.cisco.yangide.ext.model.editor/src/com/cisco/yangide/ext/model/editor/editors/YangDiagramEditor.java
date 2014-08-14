package com.cisco.yangide.ext.model.editor.editors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;

import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.Node;
import com.cisco.yangide.ext.model.editor.diagram.EditorFeatureProvider;
import com.cisco.yangide.ext.model.editor.util.DiagramImportSupport;

public class YangDiagramEditor extends DiagramEditor {

    private Module module;
    private YangDiagramModuleInfoPanel infoPane;
    private IModelChangeHandler modelChangeHandler = new IModelChangeHandler() {

        @Override
        public void nodeRemoved(Node node) {
            System.out.println("Removed " + node);
        }

        @Override
        public void nodeChanged(Node node, EAttribute attribute, Object newValue) {
            System.out.println("Changed " + node);
        }

        @Override
        public void nodeAdded(Node parent, Node child, int position) {
            System.out.println("Added " + child);
            PictogramElement[] parentShapes = getDiagramTypeProvider().getFeatureProvider()
                    .getAllPictogramElementsForBusinessObject(parent);
            ContainerShape shape = null;
            for (PictogramElement parentShape : parentShapes) {
                if (parentShape instanceof ContainerShape) {
                    shape = (ContainerShape) parentShape;
                    break;
                }
            }
            AddContext context = new AddContext();
            context.setTargetContainer(shape);
            context.setNewObject(child);
            context.setLocation(0, 0);
            getDiagramTypeProvider().getFeatureProvider().addIfPossible(context);
        }
    };

    @Override
    protected DiagramBehavior createDiagramBehavior() {
        return new YangDiagramBehavior(this);
    }

    @Override
    public void createPartControl(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;
        layout.verticalSpacing = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        layout.marginHeight = 0;
        parent.setLayout(layout);

        infoPane = new YangDiagramModuleInfoPanel(parent, module);

        super.createPartControl(parent);

        for (Control c : parent.getChildren()) {
            if (!infoPane.equals(c)) {
                c.setLayoutData(new GridData(GridData.FILL_BOTH));
            }
        }
    }

    @Override
    protected void setInput(IEditorInput input) {
        super.setInput(input);
        final URI uri = ((IDiagramEditorInput) input).getUri();
        module = ((YangDiagramEditorInput) input).getModule();
        getEditingDomain().getCommandStack().execute(new RecordingCommand(getEditingDomain()) {

            @Override
            protected void doExecute() {
                ensureDiagramResource(uri);
                importDiagram();
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

    private void importDiagram() {
        if (null != module) {
            final Diagram diagram = getDiagramTypeProvider().getDiagram();
            getDiagramTypeProvider().getFeatureProvider().link(diagram, module);
            DiagramImportSupport.importDiagram(diagram, getDiagramTypeProvider().getFeatureProvider());
        }

    }

    /**
     * @return
     */
    public IModelChangeHandler getModelChangeHandler() {
        return modelChangeHandler;
    }

    /**
     * @param sourceElementCreator the sourceElementCreator to set
     */
    public void setSourceElementCreator(ISourceElementCreator sourceElementCreator) {
        ((EditorFeatureProvider) getDiagramTypeProvider().getFeatureProvider())
        .setSourceElementCreator(sourceElementCreator);
    }
}
