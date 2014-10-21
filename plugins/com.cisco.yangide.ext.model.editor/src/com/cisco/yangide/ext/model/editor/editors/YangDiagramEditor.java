package com.cisco.yangide.ext.model.editor.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;
import org.eclipse.graphiti.ui.platform.GraphitiShapeEditPart;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;

import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.ext.model.ModelPackage;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.Node;
import com.cisco.yangide.ext.model.editor.diagram.EditorFeatureProvider;
import com.cisco.yangide.ext.model.editor.util.DiagramImportSupport;
import com.cisco.yangide.ext.model.editor.util.LayoutUtil;
import com.cisco.yangide.ext.model.editor.util.YangModelUIUtil;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class YangDiagramEditor extends DiagramEditor {

    private Module module;
    private YangDiagramModuleInfoPanel infoPane;
    private ISourceModelManager sourceModelManager;
    private final YangEditor sourceEditor;
    
    public YangDiagramEditor(YangEditor sourceEditor) {
        super();
        this.sourceEditor = sourceEditor;
    }

    private IModelChangeHandler modelChangeHandler = new IModelChangeHandler() {

        @Override
        public void nodeRemoved(Node node) {
            System.out.println("Removed " + node);
            PictogramElement[] elements = getDiagramTypeProvider().getFeatureProvider()
                    .getAllPictogramElementsForBusinessObject(node);
            for (PictogramElement element : elements) {
                RemoveContext context = new RemoveContext(element);
                IRemoveFeature feature = getDiagramTypeProvider().getFeatureProvider().getRemoveFeature(context);
                getDiagramBehavior().executeFeature(feature, context);
            }
            infoPane.update();
        }

        @Override
        public void nodeChanged(Node node, EObject object, Object newValue) {
            System.out.println("Changed " + node);
            if (object instanceof EAttribute) {
                PictogramElement pe = YangModelUIUtil.getBusinessObjectPropShape(getDiagramTypeProvider()
                        .getFeatureProvider(), node, (EAttribute)object);
                if (null != pe) {
                    YangModelUIUtil.updatePictogramElement(getDiagramTypeProvider().getFeatureProvider(), pe);
                }
            }
            infoPane.update();
        }

        @Override
        public void nodeAdded(Node parent, Node child, int position) {
            System.out.println("Added " + child);
            if (null == YangModelUIUtil.getBusinessObjectShape(getDiagramTypeProvider().getFeatureProvider(), child)) {
                Point p = null;
                if (parent instanceof Module) {
                    p = ((YangDiagramBehavior) getDiagramBehavior()).getCreatePosition();
                }
                PictogramElement pe = YangModelUIUtil.getBusinessObjectShape(getDiagramTypeProvider()
                        .getFeatureProvider(), parent);
                if (null != pe && pe instanceof ContainerShape) {
                    YangModelUIUtil.drawObject(child, (ContainerShape) pe, getDiagramTypeProvider()
                            .getFeatureProvider(), null == p ? 0 : p.x, null == p ? 0 : p.y);
                    if (pe instanceof Diagram && null == p) {
                        getEditingDomain().getCommandStack().execute(new RecordingCommand(getEditingDomain()) {

                            @Override
                            protected void doExecute() {
                                LayoutUtil.layoutDiagram(getDiagramTypeProvider().getFeatureProvider());
                            }
                        });

                    }
                }
                ((YangDiagramBehavior) getDiagramBehavior()).setCreatePosition(null);
                infoPane.update();
            }

        }
    };
    private URI uri;

    @Override
    protected DiagramBehavior createDiagramBehavior() {
        return new YangDiagramBehavior(this);
    }

    private boolean layouted = false;
    private Diagram diagram;
    private Point diagramSize = new Point(1200, 200);
    private SourceSelectionUpdater sourceSelectionUpdater;

    @Override
    public void createPartControl(final Composite parent) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.makeColumnsEqualWidth = false;
        layout.verticalSpacing = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        layout.marginHeight = 0;
        parent.setLayout(layout);

        infoPane = new YangDiagramModuleInfoPanel(parent, module, getFile());

        super.createPartControl(infoPane.getDiagram());

        for (Control c : infoPane.getDiagram().getChildren()) {
            if (!infoPane.equals(c)) {
                c.setLayoutData(new GridData(GridData.FILL_BOTH));
            }
        }

        final GraphicalViewer viewer = (GraphicalViewer) YangDiagramEditor.this.getAdapter(GraphicalViewer.class);
        viewer.getControl().addControlListener(new ControlListener() {

            @Override
            public void controlResized(ControlEvent e) {
                if (viewer.getControl().isDisposed()) {
                    return;
                }

                diagramSize = viewer.getControl().getSize();

                ((EditorFeatureProvider) getDiagramTypeProvider().getFeatureProvider()).updateDiagramSize(
                        diagramSize.x, diagramSize.y);
                if (!layouted && diagramSize.x != 0 && diagramSize.y != 0) {
                    layouted = true;
                    YangModelUIUtil.layoutPictogramElement(diagram, getDiagramTypeProvider().getFeatureProvider());
                    viewer.select(viewer.getRootEditPart());
                    viewer.reveal(viewer.getRootEditPart());
                }
            }

            @Override
            public void controlMoved(ControlEvent e) {
            }
        });
        loadDiagram();
    }

    @Override
    public void initializeGraphicalViewer() {
        super.initializeGraphicalViewer();
        GraphicalViewer graphicalViewer = getGraphicalViewer();
        if (graphicalViewer != null) {
            graphicalViewer.addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    ((YangDiagramBehavior) getDiagramBehavior()).getYangPaletteBehavior().updateSelection(
                            event.getSelection());
                }
            });
        }
        sourceSelectionUpdater = new SourceSelectionUpdater();
    }

    @Override
    protected void setInput(IEditorInput input) {
        super.setInput(input);
        uri = ((IDiagramEditorInput) input).getUri();
        module = ((YangDiagramEditorInput) input).getModule();
        // added self changes listener
        module.eAdapters().add(new EContentAdapter() {
            @Override
            public void notifyChanged(Notification notification) {
                if (notification.getNotifier() instanceof Node) {
                    if (notification.getFeature() == ModelPackage.Literals.NAMED_NODE__NAME
                            || notification.getFeature() == ModelPackage.Literals.USES__QNAME
                            || notification.getFeature() == ModelPackage.Literals.REFERENCE_NODE__REFERENCE
                            || notification.getFeature() == ModelPackage.Literals.SUBMODULE__BELONGS_TO) {
                        if (notification.getNewValue() != null
                                && !notification.getNewValue().equals(notification.getOldValue())) {
                            modelChangeHandler.nodeChanged((Node) notification.getNotifier(),
                                    (EObject) notification.getFeature(), notification.getNewValue());
                            final EClass type = YangModelUtil.getConnectionReferenceSubjectClass(notification
                                    .getNotifier());
                            if (null != type) {
                                getDiagramBehavior().getEditingDomain().getCommandStack()
                                .execute(new RecordingCommand(getDiagramBehavior().getEditingDomain()) {

                                    @Override
                                    protected void doExecute() {
                                        YangModelUIUtil.updateConnections(type, getDiagramTypeProvider()
                                                .getFeatureProvider());
                                    }
                                });
                            }
                        }
                    }
                }
                super.notifyChanged(notification);
            }
        });
        loadDiagram();
    }

    private void loadDiagram() {
        if (getGraphicalViewer() != null) {
            getEditingDomain().getCommandStack().execute(new RecordingCommand(getEditingDomain()) {

                @Override
                protected void doExecute() {
                    ensureDiagramResource(uri);
                    importDiagram();
                }
            });
        }
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
            diagram = getDiagramTypeProvider().getDiagram();
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

    public void setSourceModelManager(ISourceModelManager sourceModelManager) {
        ((EditorFeatureProvider) getDiagramTypeProvider().getFeatureProvider())
        .setSourceModelManager(sourceModelManager);
        this.sourceModelManager = sourceModelManager;
    }

    private IFile getFile() {
        return ((YangDiagramEditorInput) getEditorInput()).getFile();
    }

    /**
     * Enables selection of source code element that corresponds to the selected diagram shape.
     */
    public void startSourceSelectionUpdater() {
        GraphicalViewer graphicalViewer = getGraphicalViewer();
        if (graphicalViewer != null) 
            graphicalViewer.addSelectionChangedListener(sourceSelectionUpdater);
    }

    /**
     * Disables selection of source code element that corresponds to the selected diagram shape.
     */
    public void stopSourceSelectionUpdater() {
        GraphicalViewer graphicalViewer = getGraphicalViewer();
        if (graphicalViewer != null) 
            graphicalViewer.removeSelectionChangedListener(sourceSelectionUpdater);
    }

    private class SourceSelectionUpdater implements ISelectionChangedListener {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            Object object = selection.getFirstElement();
            if (object instanceof GraphitiShapeEditPart) {
                PictogramElement element = (PictogramElement) ((GraphitiShapeEditPart) object)
                        .getPictogramElement();
                EObject node = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(element);
                ASTNode moduleNode = sourceModelManager.getModuleNode((Node) node);
                IRegion region = YangEditor.getSelectionRegion(moduleNode);
                if (region != null) {
                    sourceEditor.setHighlightRange(region.getOffset(), region.getLength(), true);
                    // sourceEditor.selectAndReveal(...) doesn't work here since projection is disabled
                }
            }
        }
    }
}
