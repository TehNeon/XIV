package pw.latematt.xiv.ui.clickgui.element.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.ui.clickgui.GuiClick;
import pw.latematt.xiv.ui.clickgui.element.Element;
import pw.latematt.xiv.ui.clickgui.theme.ClickTheme;
import pw.latematt.xiv.utils.NahrFont;
import pw.latematt.xiv.utils.RenderUtils;

public class ThemeButton extends Element {
    protected static Minecraft mc = Minecraft.getMinecraft();
    protected final ClickTheme theme;

    public ThemeButton(ClickTheme theme, float x, float y, float width, float height) {
        super(x, y, width, height);

        this.theme = theme;
    }

    @Override
    public void drawElement(int mouseX, int mouseY) {
        GuiClick.getTheme().renderButton(getTheme().getName(), GuiClick.getTheme() == getTheme(), getX(), getY(), getWidth(), getHeight(), isOverElement(mouseX, mouseY));
    }

    @Override
    public void keyPressed(int key) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isOverElement(mouseX, mouseY) && mouseButton == 0) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            GuiClick.setTheme(theme);
        }
    }

    public ClickTheme getTheme() {
        return theme;
    }

    @Override
    public void onGuiClosed() {
    }
}