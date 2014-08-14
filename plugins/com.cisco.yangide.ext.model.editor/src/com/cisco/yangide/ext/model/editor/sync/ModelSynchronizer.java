/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 */
package com.cisco.yangide.ext.model.editor.sync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.merge.BatchMerger;
import org.eclipse.emf.compare.merge.IBatchMerger;
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.compare.merge.ReferenceChangeMerger;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EObject;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.editor.editors.IReconcileHandler;
import com.cisco.yangide.editor.editors.YangEditor;
import com.cisco.yangide.ext.model.Module;
import com.cisco.yangide.ext.model.Node;
import com.cisco.yangide.ext.model.editor.editors.IModelChangeHandler;
import com.cisco.yangide.ext.model.editor.editors.ISourceElementCreator;
import com.cisco.yangide.ext.model.editor.editors.YangDiagramEditor;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

/**
 * @author Konstantin Zaitsev
 * @date Aug 13, 2014
 */
public class ModelSynchronizer {
    private YangEditor yangSourceEditor;

    // modules to sync
    private com.cisco.yangide.core.dom.Module astModule;
    private Module diagModule;

    // source to diag notifier
    private IModelChangeHandler modelChangeHandler;
    private DiagramModelMergeAdapter diagModelMergeAdapter;

    /** Diag node to AST node mapping. */
    private Map<Node, ASTNode> mapping = new WeakHashMap<>();
    private boolean notification;

    private DiagramModelAdapter diagModelAdapter;

    private ISourceElementCreator sourceElementCreator = new ISourceElementCreator() {

        @Override
        public void createSourceElement(Node parent, int position, String content) {
            ASTNode node = mapping.get(parent);
            diagModelAdapter.add(node, content, position);
        }
    };

    public ModelSynchronizer(YangEditor yangSourceEditor, YangDiagramEditor yangDiagramEditor) {
        this.yangSourceEditor = yangSourceEditor;
        this.modelChangeHandler = yangDiagramEditor.getModelChangeHandler();

        this.diagModelMergeAdapter = new DiagramModelMergeAdapter(modelChangeHandler);
        this.diagModelAdapter = new DiagramModelAdapter(this, yangSourceEditor, mapping);
        this.yangSourceEditor.addReconcileHandler(new IReconcileHandler() {
            @Override
            public void reconcile() {
                syncWithSource();
            }
        });
    }

    public void disableNotification() {
        notification = false;
    }

    public void enableNotification() {
        notification = true;
    }

    public boolean isNotificationEnabled() {
        return notification;
    }

    /**
     *
     */
    public void syncWithSource() {
        if (isNotificationEnabled()) {
            try {
                disableNotification();
                try {
                    updateFromSource(yangSourceEditor.getModule(), true);
                } catch (YangModelException e) {
                    YangCorePlugin.log(e);
                }
            } finally {
                enableNotification();
            }
        }
    }

    /**
     * @return the sourceElementCreator
     */
    public ISourceElementCreator getSourceElementCreator() {
        return sourceElementCreator;
    }

    void updateFromSource(com.cisco.yangide.core.dom.Module module, boolean notify) {
        System.out.println("from source " + notify);
        final Map<Node, ASTNode> newMapping = new HashMap<>();

        astModule = module;
        Module moduleNew = YangModelUtil.exportModel(module, newMapping);
        IComparisonScope scope = new DefaultComparisonScope(diagModule, moduleNew, diagModule);
        Comparison comparison = EMFCompare.builder().build().compare(scope);

        updateMappings(newMapping, comparison.getMatches());
        List<Diff> differences = comparison.getDifferences();
        IMerger.Registry mergerRegistry = IMerger.RegistryImpl.createStandaloneInstance();
        ReferenceChangeMerger refMerger = new ReferenceChangeMerger() {
            @Override
            protected EObject createCopy(EObject referenceObject) {
                EObject copy = super.createCopy(referenceObject);
                if (newMapping.containsKey(referenceObject) && copy instanceof Node) {
                    mapping.put((Node) copy, newMapping.remove(referenceObject));
                }
                return copy;
            }
        };
        refMerger.setRanking(20); // highest rank then default
        mergerRegistry.add(refMerger);
        IBatchMerger merger = new BatchMerger(mergerRegistry);
        if (notify) {
            diagModule.eAdapters().add(diagModelMergeAdapter);
        }
        merger.copyAllRightToLeft(differences, null);
        if (notify) {
            diagModule.eAdapters().remove(diagModelMergeAdapter);
        }
    }

    private void updateMappings(Map<Node, ASTNode> newMapping, Iterable<Match> matches) {
        for (Match match : matches) {
            if (newMapping.containsKey(match.getRight())) {
                mapping.put((Node) match.getLeft(), newMapping.get(match.getRight()));
            }
            updateMappings(newMapping, match.getAllSubmatches());
        }
    }

    /**
     * @return
     */
    public Module getDiagramModule() {
        if (diagModule == null) {
            diagModule = YangModelUtil.exportModel(getSourceModule(), mapping);
            diagModule.eAdapters().add(diagModelAdapter);
        }
        return diagModule;
    }

    public com.cisco.yangide.core.dom.Module getSourceModule() {
        if (astModule == null) {
            try {
                astModule = yangSourceEditor.getModule();
            } catch (YangModelException e) {
                YangCorePlugin.log(e);
            }
        }
        return astModule;
    }
}
