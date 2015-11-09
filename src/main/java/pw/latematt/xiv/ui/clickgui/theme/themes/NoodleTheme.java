package pw.latematt.xiv.ui.clickgui.theme.themes;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.ui.clickgui.GuiClick;
import pw.latematt.xiv.ui.clickgui.element.Element;
import pw.latematt.xiv.ui.clickgui.element.elements.ModButton;
import pw.latematt.xiv.ui.clickgui.panel.Panel;
import pw.latematt.xiv.ui.clickgui.theme.ClickTheme;
import pw.latematt.xiv.utils.NahrFont;
import pw.latematt.xiv.utils.RenderUtils;
import pw.latematt.xiv.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Rederpz
 */
public class NoodleTheme extends ClickTheme {
    protected NahrFont font;

    protected GuiClick gui;

    public NoodleTheme(GuiClick gui) {
        super("Noodle", 96, 13, gui);
        this.gui = gui;
    }

    @Override
    public void renderPanel(Panel panel) {
        if (Objects.isNull(font)) {
            font = new NahrFont("Comic Sans MS", 18);
        }

        panel.setOpenHeight(15);
        panel.setButtonOffset(1.5F);
        panel.setWidth(100);

        RenderUtils.drawBorderedRect(panel.getX(), panel.getY() + 2, panel.getX() + panel.getWidth(), panel.getY() + (panel.isOpen() ? panel.getHeight() : panel.getOpenHeight()), 0xFF000000, 0x66000000);
        font.drawString(panel.getName(), panel.getX() + 3, panel.getY(), NahrFont.FontType.NORMAL, 0xFFFFFFFF);

        if (panel.isOpen()) {
            RenderUtils.drawRect(panel.getX() + 1, panel.getY() + panel.getOpenHeight(), panel.getX() + panel.getWidth() - 1, panel.getY() + panel.getOpenHeight() + 0.5F, 0xFF000000);
        }

        RenderUtils.drawBorderedGradientRect(panel.getX() + panel.getWidth() - panel.getOpenHeight() + 2.5F, panel.getY() + 3.5F, panel.getX() + panel.getWidth() - 1.5F, panel.getY() + panel.getOpenHeight() - 1.5F, 0xFF000000, 0xFF212121, 0xFF212121);
        font.drawString(panel.isOpen() ? "^" : "v", panel.getX() + panel.getWidth() - (panel.isOpen() ? 9.75F : 9.50F), panel.getY() + (panel.isOpen() ? 1 : -2), NahrFont.FontType.NORMAL, 0xFFFFFFFF);
    }

    @Override
    public void renderButton(String name, boolean enabled, float x, float y, float width, float height, boolean overElement, Element element) {
        if (Objects.isNull(font)) {
            font = new NahrFont("Comic Sans MS", 18);
        }

        element.setWidth(this.getElementWidth());
        element.setHeight(this.getElementHeight());

        RenderUtils.drawBorderedGradientRect(x, y, x + 96, y + getElementHeight(), 0xFF000000, enabled ? 0xFF5AACEB : 0xFF232323, enabled ? 0xFF1466A5 : 0xFF212121);

        font.drawString(name, x + 2, y - 3, NahrFont.FontType.NORMAL, 0xFFFFFFFF);
        if(element instanceof ModButton) {
            ModButton butt = (ModButton) element;


            List<Value> values = new ArrayList<>();

            for(Value val: XIV.getInstance().getValueManager().getContents()) {
                if(val.getName().toLowerCase().startsWith(butt.getMod().getName().toLowerCase())) {
                    if(!(val.getValue() instanceof Enum)) {
                        values.add(val);
                    }
                }
            }

            if(values.size() > 0) {
                font.drawString(butt.open ? "-" : "+", x + element.getWidth() - 8, y - 3, NahrFont.FontType.NORMAL, 0xFFFFFFFF);

                element.setHeight(butt.open ? this.getElementHeight() * (values.size() + 1) : this.getElementHeight());
            }
        }
    }

    @Override
    public void renderSlider(String name, float value, float x, float y, float width, float height, float sliderX, boolean overElement, Element element) {
        if (Objects.isNull(font)) {
            font = new NahrFont("Comic Sans MS", 18);
        }

        element.setWidth(96);
        element.setHeight(this.getElementHeight());

        RenderUtils.drawBorderedRect(x, y + 1, x + element.getWidth(), y + height, 0x801E1E1E, 0xFF212121);
        RenderUtils.drawBorderedRect(x, y + 1, x + sliderX, y + height, 0x801E1E1E, 0xFF5AACEB);

        font.drawString(name, x + 2, y - 1, NahrFont.FontType.SHADOW_THIN, 0xFFFFF0F0);
        font.drawString(value + "", x + element.getWidth() - font.getStringWidth(value + "") - 2, y - 1, NahrFont.FontType.SHADOW_THIN, 0xFFFFF0F0);
    }
}
