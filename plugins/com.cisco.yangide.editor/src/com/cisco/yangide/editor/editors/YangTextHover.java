package com.cisco.yangide.editor.editors;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.core.YangModelException;
import com.cisco.yangide.core.dom.ASTNode;
import com.cisco.yangide.core.dom.ContrainerSchemaNode;
import com.cisco.yangide.core.dom.GroupingDefinition;
import com.cisco.yangide.core.dom.LeafSchemaNode;
import com.cisco.yangide.core.dom.Module;
import com.cisco.yangide.core.dom.ModuleImport;
import com.cisco.yangide.core.dom.QName;
import com.cisco.yangide.core.dom.SubModule;
import com.cisco.yangide.core.dom.SubModuleInclude;
import com.cisco.yangide.core.dom.TypeDefinition;
import com.cisco.yangide.core.dom.TypeReference;
import com.cisco.yangide.core.dom.UsesNode;
import com.cisco.yangide.core.indexing.ElementIndexInfo;
import com.cisco.yangide.core.indexing.ElementIndexType;
import com.cisco.yangide.core.model.YangModelManager;

public class YangTextHover extends AbstractYangTextHover implements ITextHoverExtension{
	
	private IInformationControlCreator fHoverControlCreator;
	private IInformationControlCreator fPresenterControlCreator;
	private static String selection;

	private static final String fgStyleSheet = "/* Font definitions */\n" + //$NON-NLS-1$
	"html         { font-family: sans-serif; font-size: 9pt; font-style: normal; font-weight: normal; }\n" + //$NON-NLS-1$ 
	"body, h1, h2, h3, h4, h5, h6, p, table, td, caption, th, ul, ol, dl, li, dd, dt { font-size:1em; }\n" + //$NON-NLS-1$ 
	"pre                             { font-family: monospace; }\n" + //$NON-NLS-1$ 
	""; //$NON-NLS-1$

	
	public Object getHoverInfo2(ITextViewer textViewer, 
	                                      IRegion hoverRegion) {
	    selection = getHoverInfo(textViewer, hoverRegion);
	    return selection;
	}
	
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		String hoverInfo = null;
		YangEditor editor = (YangEditor)getEditor();
		try{
			Module module = editor.getModule();
			if(module != null){
				ASTNode node = module.getNodeAtPosition(hoverRegion.getOffset());
				hoverInfo = getHoverInfo(node);
			}
		} catch (YangModelException e) {
			YangCorePlugin.log(e);
		}
		
		return hoverInfo;
	}
	
	private String getHoverInfo(ASTNode node){		
		QName name = null;
		ElementIndexType indexType = null;
		if(node instanceof TypeReference){
			name = ((TypeReference)node).getType();
			indexType = ElementIndexType.TYPE;
		} else if (node instanceof UsesNode){
			name = ((UsesNode)node).getGrouping();
			indexType = ElementIndexType.GROUPING;
		} else if(node instanceof ModuleImport) {
			String moduleName = ((ModuleImport)node).getName();
			name = new QName(moduleName, ((ModuleImport) node).getPrefix(), moduleName, ((ModuleImport) node).getRevision());
			indexType = ElementIndexType.MODULE;
		} else if(node instanceof TypeDefinition) {
			Module module = (Module)node.getParent();
			name = new QName(module.getName(), null, ((TypeDefinition) node).getName(), module.getRevision());
			indexType = ElementIndexType.TYPE;	
		} else if (node instanceof GroupingDefinition){
			Module module = (Module)node.getParent();
			name = new QName(module.getName(), null, ((GroupingDefinition) node).getName(), module.getRevision());
			indexType = ElementIndexType.GROUPING;
		} else if (node instanceof Module) {
			/*name = new QName(((Module)node).getName(), null, ((Module)node).getName(), ((Module)node).getRevision());
			indexType = ElementIndexType.MODULE;*/
		} else if (node instanceof SubModule) {
			
		} else if (node instanceof SubModuleInclude) {
			
		} else if (node instanceof ContrainerSchemaNode) {
			
		} else if (node instanceof LeafSchemaNode) {
			
		}
		
		if(name != null) {
			ElementIndexInfo[] search = YangModelManager.search(name.getModule(), name.getRevision(), name.getName(), indexType, null, null);
			ElementIndexInfo indexInfo = null;
			if (search.length > 0) {
				indexInfo = search[0]; 		
				StringBuffer buffer = new StringBuffer();
				if(node.getNodeName() != null) {
					buffer.append("<b>" + node.getNodeName() + ": </b>");
				}
				getElementsInfo(buffer, indexInfo);
				return buffer.toString();	
			}
		}
		return null;
	}
	
	@SuppressWarnings("restriction")
	private void getElementsInfo(StringBuffer buffer, ElementIndexInfo indexInfo){
		if(indexInfo.getName() != null) {
			buffer.append("<b>" + indexInfo.getName() +" </b>");
		}
		addElementParagraph(buffer, "description", indexInfo.getDescription());
		addElementParagraph(buffer, "reference", indexInfo.getReference());
		//print module info
		if(indexInfo.getType() != ElementIndexType.MODULE && indexInfo.getModule() != null) {
			Map<String, String> moduleInfo = getModuleInfo(indexInfo.getModule(), indexInfo.getRevision());
			Set<String> keys = moduleInfo.keySet();
			for(String key: keys){
				addElementParagraph(buffer, key, moduleInfo.get(key));
			}
		}
		if (buffer.length() > 0) {
			HTMLPrinter.insertPageProlog(buffer, 0, YangTextHover.getStyleSheet());
			HTMLPrinter.addPageEpilog(buffer);
		}
	}
	
	@SuppressWarnings("restriction")
	private void addElementParagraph(StringBuffer buffer, String key, String value){
		if(value != null) {
			HTMLPrinter.addParagraph(buffer, "<b>" + key + " </b></br>" + formatIndexInfo(value));
		}
	}
	
	Map<String, String> getModuleInfo(String moduleName, String revision){
		Map<String, String> result = new HashMap<String, String>();
		ElementIndexInfo[] search = YangModelManager.search(moduleName, revision, moduleName, ElementIndexType.MODULE, null, null);
		ElementIndexInfo indexInfo = null;
		if (search.length > 0) {
			indexInfo = search[0]; 
			result.put("module", indexInfo.getModule());
			result.put("revision", indexInfo.getRevision());
			result.put("description", indexInfo.getDescription());
		}
		return result;
	}
	
	
	
	private String formatIndexInfo(String source){
		String result = "";	
		boolean newParagraph = false;
		if(!source.isEmpty()) newParagraph = true;
		else return source;
		
		Scanner scanner = new Scanner(source);
		String line = null;
		while(scanner.hasNextLine()){
			line = scanner.nextLine();
			if(line.isEmpty()){
				result += "</p>";
				newParagraph = true;
			} else {
				if(newParagraph){
					result += "<p>";
					newParagraph = false;
				}
				result += line;
			}
		}
		scanner.close();
		return result;
	}
	
	@SuppressWarnings("restriction")
	private static String getStyleSheet() {
		String css= fgStyleSheet;
		if (css != null) {
			FontData fontData= JFaceResources.getFontRegistry().getFontData(JFaceResources.DEFAULT_FONT)[0];
			css= HTMLPrinter.convertTopLevelFont(css, fontData);
		}

		return css;
	}
	
	public IInformationControlCreator getInformationPresenterControlCreator() {
		if (fPresenterControlCreator == null) {
			fPresenterControlCreator= new PresenterControlCreator();
		}
		return fPresenterControlCreator;
	}
	
	public static final class PresenterControlCreator extends AbstractReusableInformationControlCreator {

		public PresenterControlCreator() {
		}

		@SuppressWarnings("restriction")
		@Override
		public IInformationControl doCreateInformationControl(Shell parent) {
			if (BrowserInformationControl.isAvailable(parent)) {
				ToolBarManager tbm = new ToolBarManager(SWT.FLAT);
				String font = JFaceResources.DEFAULT_FONT;
				BrowserInformationControl iControl= new BrowserInformationControl(parent, font, tbm);
				iControl.setInput(selection);
				iControl.setSizeConstraints(100, 100);		
				iControl.computeSizeHint();		
				return iControl;

			} else {
				return new DefaultInformationControl(parent, true);
			}
		}
	}
	
	public static final class HoverControlCreator extends AbstractReusableInformationControlCreator {
		
		private final IInformationControlCreator fInformationPresenterControlCreator;
		
		public HoverControlCreator(IInformationControlCreator informationPresenterControlCreator) {
			this(informationPresenterControlCreator, false);
		}

		public HoverControlCreator(IInformationControlCreator informationPresenterControlCreator, boolean additionalInfoAffordance) {
			fInformationPresenterControlCreator= informationPresenterControlCreator;
		}

		@SuppressWarnings("restriction")
		@Override
		public IInformationControl doCreateInformationControl(Shell parent) {
			if (BrowserInformationControl.isAvailable(parent)) {
				String font= JFaceResources.DEFAULT_FONT; 
				BrowserInformationControl iControl= new BrowserInformationControl(parent, font, EditorsUI.getTooltipAffordanceString()) {
					@Override
					public IInformationControlCreator getInformationPresenterControlCreator() {
						return fInformationPresenterControlCreator;
					}
				};
				return iControl;
			} else {
				return new DefaultInformationControl(parent, EditorsUI.getTooltipAffordanceString());
			}
		}

		@Override
		public boolean canReuse(IInformationControl control) {
			if (!super.canReuse(control))
				return false;
			return true;
		}
	}

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		if (fHoverControlCreator == null) {
			fHoverControlCreator= new HoverControlCreator(getInformationPresenterControlCreator());
		}
		return fHoverControlCreator;
	}

	
}
