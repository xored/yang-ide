package com.cisco.yangide.editor.editors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IColorManagerExtension;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Complete JavaColorManager copy
 * 
 * @author Alexey Kholupko
 */
public class YangColorManager implements IColorManager, IColorManagerExtension {

    protected Map<String, RGB> fKeyTable = new HashMap<String, RGB>(10);
    protected Map<Display, Map<RGB, Color>> fDisplayTable = new HashMap<Display, Map<RGB, Color>>(2);

    /**
     * Flag which tells if the colors are automatically disposed when the current display gets
     * disposed.
     */
    private boolean fAutoDisposeOnDisplayDispose;

    /**
     * Creates a new Java color manager which automatically disposes the allocated colors when the
     * current display gets disposed.
     */
    public YangColorManager() {
        this(true);
    }

    /**
     * Creates a new Java color manager.
     *
     * @param autoDisposeOnDisplayDispose if <code>true</code> the color manager automatically
     * disposes all managed colors when the current display gets disposed and all calls to
     * {@link org.eclipse.jface.text.source.ISharedTextColors#dispose()} are ignored.
     * @since 2.1
     */
    public YangColorManager(boolean autoDisposeOnDisplayDispose) {
        fAutoDisposeOnDisplayDispose = autoDisposeOnDisplayDispose;
    }

    public void dispose(Display display) {
        Map<RGB, Color> colorTable = fDisplayTable.get(display);
        if (colorTable != null) {
            Iterator<Color> e = colorTable.values().iterator();
            while (e.hasNext()) {
                Color color = e.next();
                if (color != null && !color.isDisposed())
                    color.dispose();
            }
        }
    }

    /*
     * @see IColorManager#getColor(RGB)
     */
    public Color getColor(RGB rgb) {

        if (rgb == null)
            return null;

        final Display display = Display.getCurrent();
        Map<RGB, Color> colorTable = fDisplayTable.get(display);
        if (colorTable == null) {
            colorTable = new HashMap<RGB, Color>(10);
            fDisplayTable.put(display, colorTable);
            if (fAutoDisposeOnDisplayDispose) {
                display.disposeExec(new Runnable() {
                    public void run() {
                        dispose(display);
                    }
                });
            }
        }

        Color color = colorTable.get(rgb);
        if (color == null) {
            color = new Color(Display.getCurrent(), rgb);
            colorTable.put(rgb, color);
        }

        return color;
    }

    /*
     * @see IColorManager#dispose
     */
    public void dispose() {
        if (!fAutoDisposeOnDisplayDispose)
            dispose(Display.getCurrent());
    }

    /*
     * @see IColorManager#getColor(String)
     */
    public Color getColor(String key) {

        if (key == null)
            return null;

        RGB rgb = fKeyTable.get(key);
        return getColor(rgb);
    }

    /*
     * @see IColorManagerExtension#bindColor(String, RGB)
     */
    public void bindColor(String key, RGB rgb) {
        Object value = fKeyTable.get(key);
        if (value != null)
            throw new UnsupportedOperationException();

        fKeyTable.put(key, rgb);
    }

    /*
     * @see IColorManagerExtension#unbindColor(String)
     */
    public void unbindColor(String key) {
        fKeyTable.remove(key);
    }

}
