package com.cisco.yangide.editor.editors;

import org.eclipse.jface.text.rules.IWordDetector;

public class YangWordDetector implements IWordDetector {

    public boolean isWordPart(char character) {
        return Character.isJavaIdentifierPart(character);
    }

    public boolean isWordStart(char character) {
        return Character.isJavaIdentifierPart(character);
    }

}
