package pw.latematt.xiv.ui.clickgui.panel.panels;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.ui.clickgui.GuiClick;
import pw.latematt.xiv.ui.clickgui.element.elements.ValueButton;
import pw.latematt.xiv.ui.clickgui.panel.Panel;
import pw.latematt.xiv.value.Value;

import java.util.ArrayList;

/**
 * @author Matthew
 */
public class ESPPanel extends Panel {
    public ESPPanel(float x, float y, float width, float height) {
        super("ESP", new ArrayList<>(), x, y, width, height);

        addValueElements("esp_");
    }
}
