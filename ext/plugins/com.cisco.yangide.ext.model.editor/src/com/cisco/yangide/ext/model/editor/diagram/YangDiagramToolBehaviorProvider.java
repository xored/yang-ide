package com.cisco.yangide.ext.model.editor.diagram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.pattern.CreateFeatureForPattern;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;

import com.cisco.yangide.ext.model.editor.features.DirectEditingOnDoubleClickFeature;
import com.cisco.yangide.ext.model.editor.patterns.objects.DomainObjectPattern;
import com.cisco.yangide.ext.model.editor.util.PropertyUtil;
import com.cisco.yangide.ext.model.editor.util.YangModelUtil;

public class YangDiagramToolBehaviorProvider extends DefaultToolBehaviorProvider {
    
    private List<EClass> excludeFromPalette = Arrays.asList(YangModelUtil.MODEL_PACKAGE.getListKey());
    
    private IPaletteCompartmentEntry[] result;

    public YangDiagramToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
        super(diagramTypeProvider);
    }

    @Override
    public IContextButtonPadData getContextButtonPad(IPictogramElementContext context) {
		// remove button pads over diagram elements
        return null;
    }

    @Override
    public IPaletteCompartmentEntry[] getPalette() {
        if (null == result) {
            List<IPaletteCompartmentEntry> resultList = new ArrayList<IPaletteCompartmentEntry>();
            PaletteCompartmentEntry containers = new PaletteCompartmentEntry("Container", null);

            PaletteCompartmentEntry data = new PaletteCompartmentEntry("Data node", null);

            for (ICreateFeature cf : getFeatureProvider().getCreateFeatures()) {
                if (cf instanceof CreateFeatureForPattern
                        && ((CreateFeatureForPattern) cf).getPattern() instanceof DomainObjectPattern) {
                    DomainObjectPattern pattern = (DomainObjectPattern) ((CreateFeatureForPattern) cf).getPattern();
                    if (!excludeFromPalette.contains(pattern.getObjectEClass())) {
                        ObjectCreationToolEntry objectCreationToolEntry = new ObjectCreationToolEntry(
                                cf.getCreateName(), cf.getCreateDescription(), cf.getCreateImageId(),
                                cf.getCreateLargeImageId(), cf);
                        if (YangModelUtil.checkType(YangModelUtil.MODEL_PACKAGE.getContainingNode(),
                                pattern.getObjectEClass())) {
                            containers.getToolEntries().add(objectCreationToolEntry);
                        } else {
                            data.getToolEntries().add(objectCreationToolEntry);
                        }
                    }
                }
            }

            resultList.add(containers);
            resultList.add(data);
            result = resultList.toArray(new IPaletteCompartmentEntry[resultList.size()]);
        }

        return result;
    }

    @Override
    public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
        if (PropertyUtil.isObjectShapeProp(context.getInnerPictogramElement(), PropertyUtil.EDITABLE_SHAPE)) {
            return new DirectEditingOnDoubleClickFeature(getFeatureProvider());
        }
        return super.getDoubleClickFeature(context);
    }

}
