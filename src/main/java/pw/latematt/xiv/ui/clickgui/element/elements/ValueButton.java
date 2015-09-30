package pw.latematt.xiv.ui.clickgui.element.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.ui.clickgui.GuiClick;
import pw.latematt.xiv.ui.clickgui.element.Element;
import pw.latematt.xiv.utils.NahrFont;
import pw.latematt.xiv.utils.RenderUtils;
import pw.latematt.xiv.value.Value;

public class ValueButton extends Element {
    private static Minecraft mc = Minecraft.getMinecraft();
    private final Value<Boolean> value;
    private final String valuePrettyName;

    public ValueButton(Value<Boolean> value, String valuePrettyName, float x, float y, float width, float height) {
        super(x, y, width, height);

        this.value = value;
        this.valuePrettyName = valuePrettyName;
    }

    @Override
    public void drawElement(int mouseX, int mouseY) {
        GuiClick.getTheme().renderValueButton(this, mouseX, mouseY);
    }

    @Override
    public void keyPressed(int key) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isOverElement(mouseX, mouseY) && mouseButton == 0) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            value.setValue(!value.getValue());
        }
    }

    public Value<Boolean> getValue() {
        return value;
    }

    public String getValuePrettyName() {
        return valuePrettyName;
    }

    @Override
    public void onGuiClosed() {
    }
}
