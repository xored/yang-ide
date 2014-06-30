package com.cisco.yangide.editor.editors;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import com.cisco.yangide.editor.preferences.YangDocumentSetupParticipant;

/**
 * @author Alexey Kholupko
 *
 */
public class YangDocumentProvider extends FileDocumentProvider {

    protected IDocument createDocument(Object element) throws CoreException {
        IDocument document = super.createDocument(element);

        if (document != null) {
            IDocumentSetupParticipant partiticipant = new YangDocumentSetupParticipant();
            partiticipant.setup(document);
        }
        return document;
    }
}