package com.cisco.yangide.editor.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

public class YANGDocumentProvider extends FileDocumentProvider {

	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new YANGPartitionScanner(),
					new String[] {
						YANGPartitionScanner.YANG_COMMENT,
						YANGPartitionScanner.YANG_IDENTIFIER,
			            YANGPartitionScanner.YANG_KEYWORD,
			            YANGPartitionScanner.YANG_STRING });
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
}