/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.cisco.yangide.editor.editors.text.help;

import static com.cisco.yangide.editor.editors.text.help.YangLanguageHelpLoader.DefinitionKind.KEYWORD;
import static com.cisco.yangide.editor.editors.text.help.YangLanguageHelpLoader.DefinitionKind.TYPE;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.swt.graphics.Image;

import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.dom.SimpleNode;
import com.cisco.yangide.core.dom.SubModule;
import com.cisco.yangide.core.dom.SubModuleInclude;
import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.core.model.YangModelManager;
import com.cisco.yangide.core.model.YangModelUtil;
import com.cisco.yangide.core.parser.YangParserUtil;
import com.cisco.yangide.editor.YangEditorPlugin;
import com.cisco.yangide.editor.editors.YangScanner;
import com.cisco.yangide.editor.editors.text.Symbols;
import com.cisco.yangide.editor.editors.text.YangHeuristicScanner;
import com.cisco.yangide.editor.editors.text.YangIndenter;
import com.cisco.yangide.editor.templates.GeneralContextType;
import com.cisco.yangide.editor.templates.YangTemplateAccess;
import com.cisco.yangide.ui.internal.IYangUIConstants;
import com.cisco.yangide.ui.internal.YangUIImages;

/**
 * @author Alexey Kholupko
 * 
 * TODO: 'base' keyword completion proposals: existing 'identity' 
 */
public class YangSimpleCompletionProcessor extends TemplateCompletionProcessor implements IContentAssistProcessor {

    private final static ICompletionProposal[] NO_PROPOSALS = new ICompletionProposal[0];

    /**
     * Simple content assist tip closer. The tip is valid in a range of 5 characters around its
     * popup location.
     */
    protected static class Validator implements IContextInformationValidator {

        protected int fInstallOffset;

        /*
         * @see IContextInformationValidator#isContextInformationValid(int)
         */
        @Override
        public boolean isContextInformationValid(int offset) {
            return Math.abs(fInstallOffset - offset) < 5;
        }

        /*
         * @see IContextInformationValidator#install(IContextInformation, ITextViewer, int)
         */
        @Override
        public void install(IContextInformation info, ITextViewer viewer, int offset) {
            fInstallOffset = offset;
        }
    };

    private Comparator<ICompletionProposal> proposalComparator = new Comparator<ICompletionProposal>() {
        @Override
        public int compare(ICompletionProposal o1, ICompletionProposal o2) {

            String string1 = o1.getDisplayString();
            String string2 = o2.getDisplayString();
            return string1.compareToIgnoreCase(string2);
        }
    };

    private static class TypedProposalsList {
        List<ICompletionProposal> proposals;
        CompletionKind type;
    }

    private final static String[] fgKeywordProposals = YangScanner.getKeywords();

    private final static String[] fgBuiltinTypes = YangScanner.getTypes();

    private final static Map<String, List<String>> keywordHierarchyMap = createKeywordHierarchyMap();

    enum CompletionKind {
        None, KeywordScope, Import, Type, Uses, Include, BelongsTo, Keyword
    }

    protected IContextInformationValidator fValidator = new Validator();

    private ITextViewer viewer;

    private int cursorPosition;

    private int lineNumber;

    private String currentPrefix = null;

    /**
     * The proposal mode for the current content assist
     *
     * @see #determineProposalMode(IDocument, int, String)
     */
    private CompletionKind currentProposalMode = CompletionKind.None;

    private Module module = null;

    /*
     * (non-Javadoc) Method declared on IContentAssistProcessor
     */
    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {

        this.viewer = viewer;

        currentPrefix = null;

        TypedProposalsList result = determineProposals();
        if (result == null) {
            return NO_PROPOSALS;
        }
        switch (result.type) {
        case KeywordScope:
            return mergeProposals(result.proposals, determineTemplateProposalsForContext(documentOffset));
        default:
            if (result.proposals == null) {
                return NO_PROPOSALS;
            }
            return result.proposals.toArray(new ICompletionProposal[0]);
        }

    }

    /**
     * @return
     */
    private static Map<String, List<String>> createKeywordHierarchyMap() {
        Map<String, List<String>> keywordHierarchyMap = new HashMap<String, List<String>>();

        keywordHierarchyMap.put(
                "module",
                Arrays.asList(new String[] { "anyxml", "augment", "choice", "contact", "container", "description",
                        "deviation", "extension", "feature", "grouping", "identity", "import", "include", "leaf",
                        "leaf-list", "list", "namespace", "notification", "organization", "prefix", "reference",
                        "revision", "rpc", "typedef", "uses", "yang-version" }));
        keywordHierarchyMap.put("import", Arrays.asList(new String[] { "prefix", "revision-date" }));
        keywordHierarchyMap.put("include", Arrays.asList(new String[] { "revision-date" }));
        keywordHierarchyMap.put("revision", Arrays.asList(new String[] { "description", "reference" }));
        keywordHierarchyMap.put(
                "submodule",
                Arrays.asList(new String[] { "anyxml", "augment", "belongs-to", "choice", "contact", "container",
                        "description", "deviation", "extension", "feature", "grouping", "identity", "import",
                        "include", "leaf", "leaf-list", "list", "notification", "organization", "reference",
                        "revision", "rpc", "typedef", "uses", "yang-version" }));
        keywordHierarchyMap.put("belongs-to", Arrays.asList(new String[] { "prefix" }));
        keywordHierarchyMap.put("typedef",
                Arrays.asList(new String[] { "default", "description", "reference", "status", "type", "units" }));
        keywordHierarchyMap.put(
                "type",
                Arrays.asList(new String[] { "bit", "enum", "length", "path", "pattern", "range", "require",
                        "instance", "type" }));
        keywordHierarchyMap.put(
                "container",
                Arrays.asList(new String[] { "anyxml", "choice", "config", "container", "description", "grouping",
                        "if-feature", "leaf", "leaf-list", "list", "must", "presence", "reference", "status",
                        "typedef", "uses", "when" }));
        keywordHierarchyMap.put("must",
                Arrays.asList(new String[] { "description", "error-app-tag", "error-message", "reference" }));
        keywordHierarchyMap.put(
                "leaf",
                Arrays.asList(new String[] { "config", "default", "description", "if-feature", "mandatory", "must",
                        "reference", "status", "type", "units", "when" }));
        keywordHierarchyMap.put(
                "leaf-list",
                Arrays.asList(new String[] { "config", "description", "if-feature", "max-elements", "min-elements",
                        "must", "ordered-by", "reference", "status", "type", "units", "when" }));
        keywordHierarchyMap.put(
                "list",
                Arrays.asList(new String[] { "anyxml", "choice", "config", "container", "description", "grouping",
                        "if-feature", "key", "leaf", "leaf-list", "list", "max-elements", "min-elements", "must",
                        "ordered-by", "reference", "status", "typedef", "unique", "uses", "when" }));
        keywordHierarchyMap.put(
                "choice",
                Arrays.asList(new String[] { "anyxml", "case", "config", "container", "default", "description",
                        "if-feature", "leaf", "leaf-list", "list", "mandatory", "reference", "status", "when" }));
        keywordHierarchyMap.put(
                "case",
                Arrays.asList(new String[] { "anyxml", "choice", "container", "description", "if-feature", "leaf",
                        "leaf-list", "list", "reference", "status", "uses", "when" }));
        keywordHierarchyMap.put(
                "anyxml",
                Arrays.asList(new String[] { "config", "description", "if-feature", "mandatory", "must", "reference",
                        "status", "when" }));
        keywordHierarchyMap.put(
                "grouping",
                Arrays.asList(new String[] { "anyxml", "choice", "container", "description", "grouping", "leaf",
                        "leaf-list", "list", "reference", "status", "typedef", "uses" }));
        keywordHierarchyMap.put(
                "uses",
                Arrays.asList(new String[] { "augment", "description", "if-feature", "refine", "reference", "status",
                        "when" }));
        keywordHierarchyMap.put(
                "rpc",
                Arrays.asList(new String[] { "description", "grouping", "if-feature", "input", "output", "reference",
                        "status", "typedef" }));
        keywordHierarchyMap.put(
                "input",
                Arrays.asList(new String[] { "anyxml", "choice", "container", "grouping", "leaf", "leaf-list", "list",
                        "typedef", "uses" }));
        keywordHierarchyMap.put(
                "output",
                Arrays.asList(new String[] { "anyxml", "choice", "container", "grouping", "leaf", "leaf-list", "list",
                        "typedef", "uses" }));
        keywordHierarchyMap.put(
                "notification",
                Arrays.asList(new String[] { "anyxml", "choice", "container", "description", "grouping", "if-feature",
                        "leaf", "leaf-list", "list", "reference", "status", "typedef", "uses" }));
        keywordHierarchyMap.put(
                "notification",
                Arrays.asList(new String[] { "anyxml", "choice", "container", "description", "grouping", "if-feature",
                        "leaf", "leaf-list", "list", "reference", "status", "typedef", "uses" }));
        keywordHierarchyMap.put(
                "augment",
                Arrays.asList(new String[] { "anyxml", "case", "choice", "container", "description", "if-feature",
                        "leaf", "leaf-list", "list", "reference", "status", "uses", "when" }));
        keywordHierarchyMap.put("identity",
                Arrays.asList(new String[] { "base", "description", "reference", "status" }));
        keywordHierarchyMap.put("extension",
                Arrays.asList(new String[] { "argument", "description", "reference", "status" }));
        keywordHierarchyMap.put("argument", Arrays.asList(new String[] { "yin-element" }));
        keywordHierarchyMap.put("feature",
                Arrays.asList(new String[] { "description", "if-feature", "status", "reference" }));
        keywordHierarchyMap
                .put("deviation", Arrays.asList(new String[] { "description", "deviate", "n", "reference" }));
        keywordHierarchyMap.put(
                "deviate",
                Arrays.asList(new String[] { "config", "default", "mandatory", "max-elements", "min-elements", "must",
                        "type", "unique", "units" }));
        keywordHierarchyMap.put("range",
                Arrays.asList(new String[] { "description", "error-app-tag", "error-message", "reference" }));
        keywordHierarchyMap.put("length",
                Arrays.asList(new String[] { "description", "error-app-tag", "error-message", "reference" }));
        keywordHierarchyMap.put("pattern",
                Arrays.asList(new String[] { "description", "error-app-tag", "error-message", "reference" }));
        keywordHierarchyMap.put("enum", Arrays.asList(new String[] { "description", "reference", "status", "value" }));
        keywordHierarchyMap
                .put("bit", Arrays.asList(new String[] { "description", "reference", "status", "position" }));

        return keywordHierarchyMap;
    }

    private ICompletionProposal[] mergeProposals(List<ICompletionProposal> a, List<ICompletionProposal> b) {

        List<ICompletionProposal> combinedProposals = new ArrayList<>();
        if (a != null) {
            combinedProposals.addAll(a);
        }
        if (b != null) {
            combinedProposals.addAll(b);
        }

        // Collections.sort(combinedProposals, proposalComparator);
        return combinedProposals.toArray(new ICompletionProposal[0]);
    }

    /*
     * (non-Javadoc) Method declared on IContentAssistProcessor
     */
    @Override
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {

        IContextInformation[] result = new IContextInformation[5];
        for (int i = 0; i < result.length; i++) {
            result[i] = new ContextInformation(MessageFormat.format(
                    "{0} {1}", new Object[] { new Integer(i), new Integer(documentOffset) }), //$NON-NLS-1$
                    MessageFormat
                            .format("{0} {1}", new Object[] { new Integer(i), new Integer(documentOffset - 5), new Integer(documentOffset + 5) })); //$NON-NLS-1$
        }
        return result;
    }

    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        return new char[] { ':' };
    }

    @Override
    public char[] getContextInformationAutoActivationCharacters() {
        return new char[] { '#' };
    }

    @Override
    public IContextInformationValidator getContextInformationValidator() {
        return fValidator;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    /**
     * @return new determined proposals
     */
    private TypedProposalsList determineProposals() {
        ITextSelection selection = (ITextSelection) viewer.getSelectionProvider().getSelection();
        cursorPosition = selection.getOffset() + selection.getLength();

        IDocument doc = viewer.getDocument();
        try {
            lineNumber = doc.getLineOfOffset(cursorPosition);

            String prefix = getCurrentPrefix();
            if (prefix == null || cursorPosition == -1) {
                return null;
            }

            String previousWord = determinePreviousWord(doc, cursorPosition - prefix.length());

            return getProposalsFromDocument(doc, prefix, previousWord);
        } catch (BadLocationException e) {
            YangEditorPlugin.log(e);
        }
        return null;
    }

    private String determinePreviousWord(IDocument doc, int offset) throws BadLocationException {
        try {
            String previousText = doc.get(doc.getLineOffset(lineNumber), offset - doc.getLineOffset(lineNumber)).trim();
            String previousWord = null;
            int lastPos = previousText.lastIndexOf(" ");
            if (lastPos == -1) {
                lastPos = previousText.lastIndexOf("\t");
            }
            if (lastPos != -1) {
                previousWord = previousText.substring(lastPos);
            } else {
                previousWord = previousText;
            }
            return previousWord.trim();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * FIXME dirty and ugly
     */
    private String determineProposalScopeKeyword(IDocument doc, int offset) {
        try {
            YangHeuristicScanner yangHeuristicScanner = new YangHeuristicScanner(doc);

            int blockStartPosition = yangHeuristicScanner.findOpeningPeer(offset, '{', '}');

            int curPos = yangHeuristicScanner.findNonWhitespaceBackward(blockStartPosition - 1,
                    YangHeuristicScanner.UNBOUND);

            int currentToken = yangHeuristicScanner.previousToken(curPos, YangHeuristicScanner.UNBOUND);

            // assuming identifier between keyword and {
            int keywordEndPos = yangHeuristicScanner.getPosition();
            int keywordStartPos = keywordEndPos;

            if (currentToken == Symbols.TokenIDENT) {
                yangHeuristicScanner.previousToken(keywordEndPos, YangHeuristicScanner.UNBOUND);
                keywordStartPos = yangHeuristicScanner.getPosition();
            }
            // if there was no identifier (empty or string partition)
            if (currentToken == Symbols.TokenKEYWORD) {
                keywordEndPos = curPos + 1;
            }

            String keyword = doc.get(keywordStartPos, keywordEndPos - keywordStartPos).trim();

            return keyword;

        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @param document
     * @param prefix
     * @return proposals for the specified document
     */
    protected TypedProposalsList getProposalsFromDocument(IDocument document, String prefix, String previousWord) {
        currentProposalMode = determineProposalMode(document, cursorPosition, prefix, previousWord);
        return getProposalsByMode(prefix, currentProposalMode);
    }

    protected TypedProposalsList getProposalsByMode(String prefix, CompletionKind proposalMode) {
        TypedProposalsList proposals = null;
        switch (proposalMode) {
        case Import:
            proposals = getImportProposals(prefix, true);
            break;
        case BelongsTo:
            proposals = getImportProposals(prefix, false);
            break;
        case Type:
            proposals = getTypeProposals(prefix);
            break;
        case Uses:
            proposals = getUsesProposals(prefix);
            break;
        case Include:
            proposals = getIncludeProposals(prefix);
            break;
        case KeywordScope:
            proposals = getKeywordScopeProposals(prefix);
            break;
        default:
            break;
        }

        return proposals;
    }

    /**
     * @param prefix
     * @param addModuleRevision
     * @return importable module names
     */
    private TypedProposalsList getImportProposals(String prefix, boolean addModuleRevision) {
        // FIXME KOS: need set current project to get correct proposal
        ElementIndexInfo[] importModules = YangModelManager.search(null, null, null, ElementIndexType.MODULE, null,
                null);

        List<ICompletionProposal> moduleProposals = new ArrayList<ICompletionProposal>();

        try {

            // import, prefix or revision-date
            String previousWord = determinePreviousWord(viewer.getDocument(), cursorPosition - prefix.length());

            Map<String, String> usedImports = new HashMap<>();

            for (int i = 0; i < importModules.length; i++) {
                ElementIndexInfo info = importModules[i];

                String usedRevision = usedImports.get(info.getName());

                if (usedRevision == null || !usedRevision.equals(info.getRevision())) {
                    usedImports.put(info.getName(), info.getRevision());
                    ICompletionProposal proposal = generateImportProposal(previousWord, prefix, info, addModuleRevision);

                    if (proposal != null) {
                        moduleProposals.add(proposal);
                    }

                }
            }

            // TODO
            // if (moduleProposals.isEmpty() && previousWord != "import" && previousWord !=
            // "revision-date")
            // return getProposalsByMode(prefix, CompletionKind.KeywordScope); //or
            // CompletionKind.Keyword

        } catch (BadLocationException e) {
            YangEditorPlugin.log(e);
        }

        Collections.sort(moduleProposals, proposalComparator);
        TypedProposalsList result = new TypedProposalsList();
        result.proposals = moduleProposals;
        result.type = CompletionKind.Import;
        return result;

    }

    private ICompletionProposal generateImportProposal(String previousWord, String prefix, ElementIndexInfo info,
            boolean addModuleRevision) {

        ICompletionProposal resultProposal = null;

        String importModuleName = info.getName();
        String revision = info.getRevision();

        String displayString = importModuleName;
        String replacementString = importModuleName;

        // default for "import"
        int replacementOffset = cursorPosition - prefix.length();
        int replacementLength = prefix.length();
        int replCursorPosition = cursorPosition;

        ASTNode nodeAtPos;
        switch (previousWord) {
        case "import":
            if (revision != null && !revision.isEmpty()) {
                String moduleRevision = "";
                if (addModuleRevision) {
                    moduleRevision = "revision-date " + revision + "; ";
                }

                String modulePrefix = YangModelUtil.retrieveModulePrefix(info);
                replacementString = importModuleName + " { prefix " + modulePrefix + "; " + moduleRevision + "}";
                displayString = importModuleName += " (" + revision + ")";
                replCursorPosition = replacementString.length();
                break;
            }

        case "revision-date":
            nodeAtPos = module.getNodeAtPosition(cursorPosition);
            if (nodeAtPos instanceof ModuleImport) {
                if (importModuleName.equals(((ModuleImport) nodeAtPos).getName())) {
                    replacementString = revision + "; ";
                    displayString = revision;
                    replCursorPosition = revision.length();
                    break;
                }
            }

        case "prefix":
            nodeAtPos = module.getNodeAtPosition(cursorPosition);
            if (nodeAtPos instanceof ModuleImport) {
                if (importModuleName.equals(((ModuleImport) nodeAtPos).getName())) {
                    String modulePrefix = YangModelUtil.retrieveModulePrefix(info);
                    replacementString = modulePrefix + "; ";
                    displayString = importModuleName;
                    replCursorPosition = replacementString.length();
                    break;
                }
            }

        default:
            return null;
        }

        if (prefix.length() == 0 || importModuleName.startsWith(prefix)) {
            resultProposal = new YangCompletionProposal(replacementString, replacementOffset, replacementLength,
                    replCursorPosition, YangUIImages.getImage(IYangUIConstants.IMG_IMPORT_PROPOSAL), displayString,
                    IndexInfoProposalHelpGenerator.module(info));
        }

        return resultProposal;
    }

    /**
     * @param prefix
     * @return general keyword proposals
     */
    private TypedProposalsList getKeywordScopeProposals(String prefix) {

        String scopeKeyword = determineProposalScopeKeyword(viewer.getDocument(), cursorPosition);

        List<String> keywordProposals = YangSimpleCompletionProcessor.keywordHierarchyMap.get(scopeKeyword);

        if (keywordProposals == null) {
            keywordProposals = Arrays.asList(fgKeywordProposals);
        }

        List<ICompletionProposal> proposalsList = new ArrayList<ICompletionProposal>();
        for (String proposal : keywordProposals) {
            if (proposal.startsWith(prefix)) {
                String replacement = proposal + ' ';
                proposalsList.add(new YangCompletionProposal(replacement, cursorPosition - prefix.length(), prefix
                        .length(), replacement.length(), YangUIImages.getImage(IYangUIConstants.IMG_KEYWORD_PROPOSAL),
                        proposal, new LanguageProposalHelpGenerator(proposal, KEYWORD)));
            }
        }

        Collections.sort(proposalsList, proposalComparator);

        TypedProposalsList result = new TypedProposalsList();
        result.proposals = proposalsList;
        result.type = CompletionKind.KeywordScope;
        return result;
    }

    private TypedProposalsList getTypeProposals(String prefix) {
        // bult-in types
        List<ICompletionProposal> bltInTypesProposals = new ArrayList<ICompletionProposal>();
        for (String proposal : fgBuiltinTypes) {
            if (proposal.startsWith(prefix)) {
                bltInTypesProposals.add(new YangCompletionProposal(proposal, cursorPosition - prefix.length(), prefix
                        .length(), proposal.length(), YangUIImages.getImage(IYangUIConstants.IMG_TYPE_PROPOSAL),
                        proposal, new LanguageProposalHelpGenerator(proposal, TYPE))); 
            }
        }
        Collections.sort(bltInTypesProposals, proposalComparator);

        // custom defined by user in typedef types
        ElementIndexInfo[] customTypes = YangModelManager.search(null, null, null, ElementIndexType.TYPE, null, null);

        List<ICompletionProposal> customTypesProposals = new ArrayList<ICompletionProposal>();

        Set<String> addedCustomType = new HashSet<>();
        for (int i = 0; i < customTypes.length; i++) {
            ElementIndexInfo info = customTypes[i];

            if (addedCustomType.add(info.getName())) {

                String proposal = computeProposalForElement(info);
                if (proposal != null && (prefix.length() == 0 || proposal.startsWith(prefix))) {
                    customTypesProposals.add(new YangCompletionProposal(proposal, cursorPosition - prefix.length(),
                            prefix.length(), proposal.length(), YangUIImages
                                    .getImage(IYangUIConstants.IMG_CUSTOM_TYPE_PROPOSAL), proposal,
                            IndexInfoProposalHelpGenerator.type(info)));
                }
            }
        }
        Collections.sort(customTypesProposals, proposalComparator);

        TypedProposalsList result = new TypedProposalsList();
        result.proposals = new ArrayList<ICompletionProposal>(bltInTypesProposals.size() + customTypesProposals.size());
        result.proposals.addAll(bltInTypesProposals);
        result.proposals.addAll(customTypesProposals);

        result.type = CompletionKind.Type;
        return result;
    }

    /**
     * @param prefix
     * @return
     */
    private TypedProposalsList getUsesProposals(String prefix) {
        ElementIndexInfo[] groupings = YangModelManager.search(null, null, null, ElementIndexType.GROUPING, null, null);

        List<ICompletionProposal> groupingProposals = new ArrayList<ICompletionProposal>();

        Set<String> addedCustomType = new HashSet<>();
        for (int i = 0; i < groupings.length; i++) {
            ElementIndexInfo info = groupings[i];
            if (addedCustomType.add(info.getName())) {

                String proposal = computeProposalForElement(info);
                if (proposal != null && (prefix.length() == 0 || proposal.startsWith(prefix))) {
                    groupingProposals.add(new YangCompletionProposal(proposal, cursorPosition - prefix.length(), prefix
                            .length(), proposal.length(),
                            YangUIImages.getImage(IYangUIConstants.IMG_GROUPING_PROPOSAL), proposal,
                            IndexInfoProposalHelpGenerator.grouping(info)));
                }
            }
        }
        Collections.sort(groupingProposals, proposalComparator);

        TypedProposalsList result = new TypedProposalsList();
        result.proposals = groupingProposals;

        result.type = CompletionKind.Uses;
        return result;
    }

    /**
     * @param prefix
     * @return
     */
    private TypedProposalsList getIncludeProposals(String prefix) {
        ElementIndexInfo[] submodules = YangModelManager.search(null, null, null, ElementIndexType.SUBMODULE, null,
                null);

        List<ICompletionProposal> submoduleProposals = new ArrayList<ICompletionProposal>();

        Set<String> addedCustomType = new HashSet<>();
        for (int i = 0; i < submodules.length; i++) {
            ElementIndexInfo info = submodules[i];
            if (addedCustomType.add(info.getName())) {
                String proposal = info.getName();

                String revision = info.getRevision();
                String displayString = proposal;
                String replacementString = proposal;
                if (revision != null && !revision.isEmpty()) {
                    replacementString = proposal + " { revision-date " + revision + "; }";
                    displayString = proposal += " (" + revision + ")";
                }

                if (prefix.length() == 0 || proposal.startsWith(prefix)) {
                    submoduleProposals.add(new YangCompletionProposal(replacementString, cursorPosition
                            - prefix.length(), prefix.length(), proposal.length(), YangUIImages
                            .getImage(IYangUIConstants.IMG_GROUPING_PROPOSAL), displayString,
                            IndexInfoProposalHelpGenerator.submodule(info)));
                }
            }
        }
        Collections.sort(submoduleProposals, proposalComparator);

        TypedProposalsList result = new TypedProposalsList();
        result.proposals = submoduleProposals;

        result.type = CompletionKind.Include;
        return result;
    }

    /**
     * Generate proposals for types, groupings, etc. If element defined in other module, which is
     * imported, adds respective prefix If element is neither imported nor local, returns null
     *
     * @param elementIndexInfo
     * @return
     */
    protected String computeProposalForElement(ElementIndexInfo elementIndexInfo) {
        String result = null;

        if (module != null) {
            if (module.getName().equals(elementIndexInfo.getModule())) {
                result = elementIndexInfo.getName();
            } else if (module instanceof SubModule) {
                SimpleNode<String> pmNode = ((SubModule) module).getParentModule();
                if (pmNode.getValue().equals(elementIndexInfo.getModule())) {
                    result = ((SubModule) module).getParentPrefix() + ":" + elementIndexInfo.getName();
                }
            } else {

                SubModuleInclude includedSubmodule = module.getIncludeByName(elementIndexInfo.getModule());
                if (includedSubmodule != null) {
                    result = elementIndexInfo.getName();
                } else {

                    ModuleImport importedModule = module.getImportByName(elementIndexInfo.getModule());

                    if (importedModule != null) {
                        result = importedModule.getPrefix() + ":" + elementIndexInfo.getName();
                    }
                }
            }
        }
        return result;
    }

    /**
     * returns proposal mode basing on current cursor position (context)
     */
    protected CompletionKind determineProposalMode(IDocument document, int cursorPosition, String prefix,
            String previousWord) {

        module = YangParserUtil.parseYangFile(viewer.getDocument().get().toCharArray());

        if (module != null) {
            ASTNode nodeAtPos = module.getNodeAtPosition(cursorPosition);

            if (nodeAtPos instanceof ModuleImport
                    && cursorPosition > nodeAtPos.getStartPosition() + nodeAtPos.getNodeName().length()) {
                return CompletionKind.Import;
            }
        }

        // Dirty previous word based determination

        if ("import".equalsIgnoreCase(previousWord)) {
            return CompletionKind.Import;
        }
        if ("belongs-to".equalsIgnoreCase(previousWord)) {
            return CompletionKind.BelongsTo;
        }
        if ("type".equalsIgnoreCase(previousWord)) {
            return CompletionKind.Type;
        }
        if ("uses".equalsIgnoreCase(previousWord)) {
            return CompletionKind.Uses;
        }
        // TODO check if only current module
        if ("include".equalsIgnoreCase(previousWord)) {
            return CompletionKind.Include;
        }

        if (Arrays.asList(fgKeywordProposals).contains(previousWord)) {
            return CompletionKind.Keyword;
        }

        return CompletionKind.KeywordScope;

    }

    /**
     * @return current prefix that should be used for completion
     */
    private String getCurrentPrefix() {
        if (currentPrefix != null) {
            return currentPrefix;
        }
        ITextSelection selection = (ITextSelection) viewer.getSelectionProvider().getSelection();
        IDocument doc = viewer.getDocument();
        return getPrefixFromDocument(doc.get(), selection.getOffset() + selection.getLength()).toLowerCase();
    }

    /**
     * @param aDocumentText the whole content of the edited file as String
     * @param anOffset the cursor position
     * @return prefix in the specified document text with respect to the specified offset
     */
    protected String getPrefixFromDocument(String aDocumentText, int anOffset) {
        if (currentPrefix != null) {
            return currentPrefix;
        }
        int startOfWordToken = anOffset;

        char token = 'a';
        if (startOfWordToken > 0) {
            token = aDocumentText.charAt(startOfWordToken - 1);
        }

        while (startOfWordToken > 0
                && (Character.isJavaIdentifierPart(token) || '.' == token || '-' == token || ';' == token || ':' == token)
                && !('$' == token)) {
            startOfWordToken--;
            if (startOfWordToken == 0) {
                break; // word goes right to the beginning of the doc
            }
            token = aDocumentText.charAt(startOfWordToken - 1);
        }

        if (startOfWordToken != anOffset) {
            currentPrefix = aDocumentText.substring(startOfWordToken, anOffset).toLowerCase();
        } else {
            currentPrefix = "";
        }
        return currentPrefix;
    }

    private List<ICompletionProposal> determineTemplateProposalsForContext(int offset) {
        ITextSelection selection = (ITextSelection) viewer.getSelectionProvider().getSelection();
        // adjust offset to end of normalized selection
        if (selection.getOffset() == offset) {
            offset = selection.getOffset() + selection.getLength();
        }

        String prefix = extractPrefix(viewer, offset);
        Region region = new Region(offset - prefix.length(), prefix.length());
        TemplateContext context = createContext(viewer, region);
        if (context == null) {
            return null;
        }

        context.setVariable("selection", selection.getText()); // name of the selection variables {line, word_selection //$NON-NLS-1$

        Template[] templates = getTemplates(context.getContextType().getId());
        List<ICompletionProposal> matches = new ArrayList<ICompletionProposal>();
        for (int i = 0; i < templates.length; i++) {
            Template template = generateIndentedTemplate(templates[i]);

            try {
                context.getContextType().validate(template.getPattern());
            } catch (TemplateException e) {
                continue;
            }
            if ((template.getName().startsWith(prefix) && template.matches(prefix, context.getContextType().getId()))) {
                matches.add(// ! all proposals should be treated
                createProposal(template, context, (IRegion) region, getRelevance(template, prefix)));
            }
        }

        Collections.sort(matches, proposalComparator);

        return matches;
    }

    /**
     * returns template with correctly indented pattern TODO should be invoked when pattern is being
     * inserted, not computed (perfomance)
     */
    private Template generateIndentedTemplate(Template template) {

        Template indentedTemplate = template;

        String pattern = template.getPattern();

        String[] patternLines = pattern.split("\n");
        int linesCount = patternLines.length;

        if (linesCount > 1) {

            YangIndenter indenteter = new YangIndenter(viewer.getDocument(), new YangHeuristicScanner(
                    viewer.getDocument()));
            StringBuffer intendation = indenteter.computeIndentation(cursorPosition);

            StringBuffer indentedPattern = new StringBuffer();
            indentedPattern.append(patternLines[0]);
            for (int i = 1; i < linesCount; i++) {
                indentedPattern.append("\n" + intendation + patternLines[i]);
            }

            indentedTemplate = new Template(template.getName(), template.getDescription(), template.getContextTypeId(),
                    indentedPattern.toString(), template.isAutoInsertable());

        }

        return indentedTemplate;
    }

    @Override
    protected String extractPrefix(ITextViewer textViewer, int offset) {
        return getPrefixFromDocument(textViewer.getDocument().get(), offset);
    }

    @Override
    protected Template[] getTemplates(String contextTypeId) {
        YangTemplateAccess access = YangTemplateAccess.getDefault();
        return access.getTemplateStore().getTemplates();
    }

    @Override
    protected TemplateContextType getContextType(ITextViewer viewer, IRegion region) {
        YangTemplateAccess access = YangTemplateAccess.getDefault();
        return access.getContextTypeRegistry().getContextType(GeneralContextType.CONTEXT_TYPE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.text.templates.TemplateCompletionProcessor#getImage(org.eclipse.jface.text
     * .templates.Template)
     */
    @Override
    protected Image getImage(Template template) {
        return YangUIImages.getImage(IYangUIConstants.IMG_TEMPLATE_PROPOSAL);
    }
}
