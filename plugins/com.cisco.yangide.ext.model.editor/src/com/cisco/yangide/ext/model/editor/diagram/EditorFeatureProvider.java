package com.cisco.yangide.ext.model.editor.diagram;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.DefaultFeatureProviderWithPatterns;

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

public class EditorFeatureProvider extends DefaultFeatureProviderWithPatterns {

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
    
}
