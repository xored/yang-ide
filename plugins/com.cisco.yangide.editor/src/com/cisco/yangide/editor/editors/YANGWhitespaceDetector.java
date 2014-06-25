package com.cisco.yangide.editor.editors;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class YANGWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}
