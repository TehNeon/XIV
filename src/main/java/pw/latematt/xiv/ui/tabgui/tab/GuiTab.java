package pw.latematt.xiv.ui.tabgui.tab;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.utils.RenderUtils;

import java.util.Comparator;
import java.util.List;

/**
 * @author Ddong
 */
public class GuiTab {
    public List<GuiItem> buttons;
    private String label;
    private int width;
    private int selected;

    public GuiTab(String label) {
        this.buttons = Lists.newArrayList();
        this.selected = 0;
        this.label = label;
    }

    public void drawTab(int x, int y, int widest, boolean selected, boolean hovered) {
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        String prefix = hovered ? "\247f" : "\247f";
        if (hovered) {
            RenderUtils.drawBorderedRect(1.5D, y - 2, widest + 6.5D, y + 10, 0x95000000, 0x854DB3FF);
        } else {
            Gui.drawRect(2, y - 2, widest + 6, y + 10, 0x951C2A36);
        }

        font.drawStringWithShadow(prefix + this.label, x, y, 0xFFFFFF);
    }

    public void drawButtons(int startY, int width, boolean selected, boolean hovered) {
        if (!selected) {
            return;
        }

        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        int y = startY;
        for (GuiItem button : this.buttons) {
            y += 12;
            if (this.width == 2) {
                this.width = font.getStringWidth(button.getName());
            }

            if (font.getStringWidth(button.getName()) < this.width) {
                continue;
            }

            this.width = font.getStringWidth(button.getName());
        }

        RenderUtils.drawHollowRect(width + 8, startY, width + this.width + 12, y, 1.0f, 0xCC000000);
        int height = startY;
        for (GuiItem button : this.buttons) {
            if (this.buttons.get(this.selected) == button) {
                RenderUtils.drawBorderedRect(width + 7.5D, height, width + this.width + 12.5D, height + 12, 0xCC000000, (this.buttons.get(this.selected) == button) ? 0x804DB3FF : 0x951C2A36);
            } else {
                Gui.drawRect(width + 8, height, width + this.width + 12, height + 12, (this.buttons.get(this.selected) == button) ? 0x804DB3FF : 0x951C2A36);
            }
            String prefix = button.getMod().isEnabled() ? "\247f" : "\2477";
            font.drawStringWithShadow(prefix + button.getName(), width + 10, height + 2, button.getMod().isEnabled() ? -3495936 : -5723992);
            height += 12;
        }
    }

    /**
     * Handle each key event inside the tab.
     *
     * @param keyCode
     */
    public void keyboard(int keyCode) {
        switch (keyCode) {
            case Keyboard.KEY_DOWN:
                ++this.selected;
                if (this.selected > this.buttons.size() - 1) {
                    this.selected = 0;
                }

                break;
            case Keyboard.KEY_UP:
                --this.selected;
                if (this.selected < 0) {
                    this.selected = this.buttons.size() - 1;
                }
                break;
            case Keyboard.KEY_RIGHT:
            case Keyboard.KEY_RETURN:
                this.buttons.get(this.selected).getMod().toggle();
                break;
        }
    }

    public String getTabName() {
        return this.label;
    }

    public void addButton(GuiItem button) {
        this.buttons.add(button);
        this.buttons.sort(new Comparator<GuiItem>() {
            @Override
            public int compare(GuiItem o1, GuiItem o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
}
