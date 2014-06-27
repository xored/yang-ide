package com.cisco.yangide.editor.editors;

import org.eclipse.jface.text.rules.IWordDetector;

public class YangWordDetector implements IWordDetector {

    @Override
    public boolean isWordPart(char character) {
        return Character.isJavaIdentifierPart(character);
    }

    @Override
    public boolean isWordStart(char character) {
        return Character.isJavaIdentifierPart(character);
    }

}
