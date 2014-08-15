package com.cisco.yangide.ext.model.editor.diagram;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.CreateFeatureForPattern;
import org.eclipse.graphiti.pattern.DefaultFeatureProviderWithPatterns;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.swt.graphics.Point;

import com.cisco.yangide.core.YangCorePlugin;
import com.cisco.yangide.ext.model.ContainingNode;
import com.cisco.yangide.ext.model.editor.Activator;
import com.cisco.yangide.ext.model.editor.editors.ISourceElementCreator;
import com.cisco.yangide.ext.model.editor.editors.YangDiagramBehavior;
import com.cisco.yangide.ext.model.editor.features.AddReferenceConnectionFeature;
import com.cisco.yangide.ext.model.editor.features.DiagramLayoutCustomFeature;
import com.cisco.yangide.ext.model.editor.features.DiagramLayoutFeature;
import com.cisco.yangide.ext.model.editor.features.TextDirectEditingFeature;
import com.cisco.yangide.ext.model.editor.features.UpdateTextFeature;
import com.cisco.yangide.ext.model.editor.patterns.objects.AnyxmlPattern;
import com.cisco.yangide.ext.model.editor.patterns.objects.AugmentPattern;
import com.cisco.yangide.ext.model.editor.patterns.objects.ContainerPattern;
import com.cisco.yangide.ext.model.editor.patterns.objects.DeviationPattern;
import com.cisco.yangide.ext.model.editor.patterns.objects.ExtensionPattern;
import com.cisco.yangide.ext.model.editor.patterns.objects.FeaturePattern;
import com.cisco.yangide.ext.model.editor.patterns.objects.GroupingPattern;
import com.cisco.yangide.ext.model.editor.patterns.objects.IdentityPattern;
import com.cisco.yangide.ext.model.editor.patterns.objects.LeafPattern;
import com.cisco.yangide.ext.model.editor.patterns.objects.NotificationPattern;
import com.cisco.yangide.ext.model.editor.patterns.objects.RpcIOPattern;
import com.cisco.yangide.ext.model.editor.patterns.objects.RpcPattern;
import com.cisco.yangide.ext.model.editor.patterns.objects.SubmodulePattern;
import com.cisco.yangide.ext.model.editor.patterns.objects.UsesPattern;
import com.cisco.yangide.ext.model.editor.util.YangModelUIUtil;

public class EditorFeatureProvider extends DefaultFeatureProviderWithPatterns {
    private static Map<String, String> TEMPLATES = new HashMap<>();

    private ISourceElementCreator sourceElementCreator;

    public EditorFeatureProvider(IDiagramTypeProvider dtp) {
        super(dtp);
        // objects
        addPattern(new GroupingPattern());
        addPattern(new LeafPattern());
        addPattern(new ContainerPattern());
        addPattern(new AnyxmlPattern());
        addPattern(new SubmodulePattern());
        addPattern(new RpcPattern());
        addPattern(new UsesPattern());
        addPattern(new NotificationPattern());
        addPattern(new AugmentPattern());
        addPattern(new DeviationPattern());
        addPattern(new ExtensionPattern());
        addPattern(new FeaturePattern());
        addPattern(new IdentityPattern());
        addPattern(new RpcIOPattern());
    }


    @Override
    public IAddFeature getAddFeature(IAddContext context) {
        if (context instanceof IAddConnectionContext) {
            return new AddReferenceConnectionFeature(this);
        }
        return super.getAddFeature(context);
    }


    @Override
    public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
        if (context.getPictogramElement() instanceof Shape && ((Shape) context.getPictogramElement()).getGraphicsAlgorithm() instanceof Text) {
            return new TextDirectEditingFeature(this);
        }
        return super.getDirectEditingFeature(context);
    }

    @Override
    public IUpdateFeature getUpdateFeature(IUpdateContext context) {
        if (context.getPictogramElement() instanceof Shape && ((Shape) context.getPictogramElement()).getGraphicsAlgorithm() instanceof Text) {
            return new UpdateTextFeature(this);
        }
        return super.getUpdateFeature(context);
    }

    @Override
    public ILayoutFeature getLayoutFeature(ILayoutContext context) {
        if (context.getPictogramElement() instanceof Diagram) {
            return new DiagramLayoutFeature(this);
        }
        return super.getLayoutFeature(context);
    }


    @Override
    public ICustomFeature[] getCustomFeatures(ICustomContext context) {
        return new ICustomFeature[] { new DiagramLayoutCustomFeature(this) };
    }

    @Override
    public ICreateFeature[] getCreateFeatures() {
        ICreateFeature[] ret = new ICreateFeature[0];
        List<ICreateFeature> retList = new ArrayList<ICreateFeature>();

        for (IPattern pattern : getPatterns()) {
            if (pattern.isPaletteApplicable()) {
                retList.add(new CreateFeatureForPattern(this, pattern) {
                    @Override
                    public Object[] create(ICreateContext context) {
                        YangDiagramBehavior behavior = (YangDiagramBehavior) EditorFeatureProvider.this
                                .getDiagramTypeProvider().getDiagramBehavior();
                        behavior.setCreatePosition(new Point(context.getX(), context.getY()));
                        String name = this.getPattern().getCreateName();
                        if (!TEMPLATES.containsKey(name)) {
                            TEMPLATES.put(name, getTemplateContent(name));
                        }
                        EObject parent = (EObject) getBusinessObjectForPictogramElement(context.getTargetContainer());
                        int position = YangModelUIUtil.getPositionInParent(context.getTargetContainer(),
                                context.getY(), EditorFeatureProvider.this);
                        if (parent instanceof ContainingNode) {
                            ContainingNode node = (ContainingNode) parent;
                            String template = TEMPLATES.get(name);
                            template = template.replaceAll("@name@", name + node.getChildren().size());
                            sourceElementCreator.createSourceElement(node, position, template);
                        }

                        return new Object[0];
                    }
                });
            }
        }

        ICreateFeature[] a = getCreateFeaturesAdditional();
        for (ICreateFeature element : a) {
            retList.add(element);
        }

        return retList.toArray(ret);
    }

    /**
     * @param sourceElementCreator the sourceElementCreator to set
     */
    public void setSourceElementCreator(ISourceElementCreator sourceElementCreator) {
        this.sourceElementCreator = sourceElementCreator;
    }

    /**
     * @return InputStream of template with replaced placeholders.
     * @throws IOException read errors
     */
    private String getTemplateContent(String name) {
        StringBuilder sb = new StringBuilder();

        char[] buff = new char[1024];
        int len = 0;
        Path templatePath = new Path("templates/nodes/" + name + ".txt");
        try (InputStreamReader in = new InputStreamReader(FileLocator.openStream(Activator.getDefault().getBundle(),
                templatePath, false), "UTF-8")) {
            while ((len = in.read(buff)) > 0) {
                sb.append(buff, 0, len);
            }
        } catch (IOException e) {
            YangCorePlugin.log(e);
        }

        return sb.toString();
    }

}
