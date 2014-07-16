/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors.text;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

import com.cisco.yangide.core.YangCorePlugin;

/**
 * @author Konstantin Zaitsev
 * @date Jul 16, 2014
 */
public class YangAnnotationModel extends ResourceMarkerAnnotationModel {

    private ArrayList<YangMarkerAnnotation> curOverlaid = new ArrayList<>();
    private ArrayList<YangMarkerAnnotation> prevOverlaid;
    private ArrayList<YangSyntaxAnnotation> genAnnotations = new ArrayList<>();
    private ArrayList<YangProblem> problems = new ArrayList<>();
    private IProgressMonitor monitor;

    /**
     * @param resource
     */
    public YangAnnotationModel(IResource resource) {
        super(resource);
    }

    public synchronized void init() {
        problems.clear();
    }

    public synchronized void addProblem(YangProblem problem) {
        problems.add(problem);
    }

    public synchronized void reportProblem() {
        boolean temporaryProblemsChanged = false;
        boolean isCanceled = false;

        prevOverlaid = curOverlaid;
        curOverlaid = new ArrayList<>();

        if (genAnnotations.size() > 0) {
            temporaryProblemsChanged = true;
            removeAnnotations(genAnnotations, false, true);
            genAnnotations.clear();
        }

        if (problems.size() > 0) {

            Iterator<YangProblem> e = problems.iterator();
            while (e.hasNext()) {

                if (monitor != null && monitor.isCanceled()) {
                    isCanceled = true;
                    break;
                }

                try {
                    YangProblem problem = e.next();
                    overlayMarkers(problem.getPosition(), problem.getAnnotation());
                    addAnnotation(problem.getAnnotation(), problem.getPosition(), false);
                    genAnnotations.add(problem.getAnnotation());
                } catch (BadLocationException e1) {
                    // ignore exception
                }

                temporaryProblemsChanged = true;
            }
        }
        removeMarkerOverlays(isCanceled);
        prevOverlaid = null;

        if (temporaryProblemsChanged) {
            fireModelChanged();
        }
    }

    /**
     * @param isCanceled
     */
    private void removeMarkerOverlays(boolean isCanceled) {
        if (isCanceled) {
            curOverlaid.addAll(prevOverlaid);
        } else if (prevOverlaid != null) {
            Iterator<YangMarkerAnnotation> e = prevOverlaid.iterator();
            while (e.hasNext()) {
                YangMarkerAnnotation annotation = e.next();
                annotation.markDeleted(true);
            }
        }
    }

    /**
     * @param position
     * @param annotation
     */
    private void overlayMarkers(Position position, YangSyntaxAnnotation annotation) {
        Iterator<?> it = getAnnotationIterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof YangMarkerAnnotation) {
                YangMarkerAnnotation m = (YangMarkerAnnotation) obj;
                if (m.getPosition() == position) {
                    m.setOverlay(annotation);
                    prevOverlaid.remove(m);
                    curOverlaid.add(m);
                }
            }
        }
    }

    @Override
    protected MarkerAnnotation createMarkerAnnotation(IMarker marker) {
        try {
            if (YangCorePlugin.YANGIDE_PROBLEM_MARKER.equals(marker.getType())) {
                return new YangMarkerAnnotation(marker);
            }
        } catch (CoreException e) {
            // ignore exception
        }
        return super.createMarkerAnnotation(marker);
    }
}
