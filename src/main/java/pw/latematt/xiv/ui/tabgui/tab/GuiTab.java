package pw.latematt.xiv.ui.tabgui.tab;

import net.minecraft.client.Minecraft;
import pw.latematt.xiv.ui.tabgui.GuiTabHandler;
import pw.latematt.xiv.utils.RenderUtils;

import java.util.ArrayList;

/**
 * @author Jack
 */

public class GuiTab {
    private final GuiTabHandler gui;
    private final ArrayList<GuiItem> mods = new ArrayList<>();
    private int menuHeight = 0;
    private int menuWidth = 0;
    private String tabName;

    public GuiTab(final GuiTabHandler gui, final String tabName) {
        this.gui = gui;
        this.tabName = tabName;
    }

    public void drawTabMenu(Minecraft mc, int x, int y) {
        this.countMenuSize(mc);
        float boxY = y;

        RenderUtils.drawBorderedRect(x, y, x + menuWidth, y + menuHeight, 1, 0x80000000, this.gui.getColourBody());
        for (int i = 0; i < this.mods.size(); i++) {
            if (this.gui.getSelectedItem() == i) {
                RenderUtils.drawBorderedRect(x, boxY, x + menuWidth, boxY + 12, 1, 0x80000000, this.gui.getColourBox());
            }

            mc.fontRendererObj.drawStringWithShadow((this.mods.get(i).getMod().isEnabled() ? this.gui.getColourHightlight() : this.gui.getColourNormal()) + this.mods.get(i).getName(), x + 1, y + this.gui.getTabHeight() * i + 2, 0xFFFFFFFF);

            boxY += 12;
        }
    }

    private void countMenuSize(Minecraft mc) {
        int maxWidth = 0;
        for (GuiItem mod : this.mods) {
            if (mc.fontRendererObj.getStringWidth(mod.getName()) > maxWidth) {
                maxWidth = mc.fontRendererObj.getStringWidth(mod.getName()) + 3;
            }
        }

        this.menuWidth = maxWidth;

        this.menuHeight = this.mods.size() * this.gui.getTabHeight();
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public ArrayList<GuiItem> getMods() {
        return mods;
    }
}
