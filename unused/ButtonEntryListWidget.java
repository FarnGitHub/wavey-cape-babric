package dev.tr7zw.waveycapes.config.screen.entry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.Tessellator;

public class ButtonEntryListWidget extends ExtendedEntryListWidget {
    private final List<ButtonEntry> rows = new ArrayList<>();

    public ButtonEntryListWidget(Minecraft mc, int width, int height, int top, int bottom, int itemHeight, List<ButtonWidget> buttons) {
        super(mc, width, height, top, bottom, itemHeight);
        this.startAtCenter = false;
        for (ButtonWidget button : buttons) {
            button.x = (width - button.width) / 2;
            this.rows.add(new ButtonEntry(button));
        }
    }

    public ButtonEntry getEntry(int index) {
        return this.rows.get(index);
    }

    protected int getEntryCount() {
        return this.rows.size();
    }

    public int getRowWidth() {
        return 400;
    }

    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 32;
    }
    
    public static class ButtonEntry implements ExtendedEntryListWidget.Entry {
        private final Minecraft minecraft;

        private final ButtonWidget button;

        public ButtonEntry(ButtonWidget button) {
            this.minecraft = Minecraft.INSTANCE;
            this.button = button;
        }

        public void render(int index, int x, int y, int rowWidth, int itemHeight, Tessellator tessellator, int mouseX, int mouseY, boolean isMouseOver) {
            this.button.y = y;
            this.button.render(this.minecraft, mouseX, mouseY);
        }

        public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
            return this.button.isMouseOver(this.minecraft, mouseX, mouseY);
        }

        public void mouseReleased(int index, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
            this.button.mouseReleased(mouseX, mouseY);
        }
    }
}
