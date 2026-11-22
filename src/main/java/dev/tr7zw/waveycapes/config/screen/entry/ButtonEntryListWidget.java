package dev.tr7zw.waveycapes.config.screen.entry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.Tessellator;

public class ButtonEntryListWidget extends ExtendedEntryListWidget {
    private final List<Row> rows = new ArrayList<>();

    public ButtonEntryListWidget(Minecraft mc, int width, int height, int top, int bottom, int itemHeight, List<ButtonWidget> buttons) {
        super(mc, width, height, top, bottom, itemHeight);
        this.startAtCenter = false;
        for (int index = 0; index < buttons.size(); index += 2) {
            buttons.get(index).x = width / 2 - 155;
            if(index < buttons.size() - 1) {
                buttons.get(index+1).x = width / 2 - 155 + 160;
            }
            this.rows.add(new Row(buttons.get(index), (index < buttons.size() - 1) ? buttons.get(index+1) : null));
        }
    }

    public Row getEntry(int index) {
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
    
    public static class Row implements ExtendedEntryListWidget.Entry {
        private final Minecraft minecraft;

        private final ButtonWidget leftButton;

        private final ButtonWidget rightButton;

        public Row(ButtonWidget leftButton, ButtonWidget p_i45014_2_) {
            this.minecraft = Minecraft.INSTANCE;
            this.leftButton = leftButton;
            this.rightButton = p_i45014_2_;
        }

        public void render(int index, int x, int y, int rowWidth, int itemHeight, Tessellator tessellator, int mouseX, int mouseY, boolean isMouseOver) {
            if (this.leftButton != null) {
                this.leftButton.y = y;
                this.leftButton.render(this.minecraft, mouseX, mouseY);
            }
            if (this.rightButton != null) {
                this.rightButton.y = y;
                this.rightButton.render(this.minecraft, mouseX, mouseY);
            }
        }

        public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
            boolean lClick = this.leftButton != null && this.leftButton.isMouseOver(this.minecraft, mouseX, mouseY);
            boolean rClick = this.rightButton != null && this.rightButton.isMouseOver(this.minecraft, mouseX, mouseY);
            return lClick || rClick;
        }

        public void mouseReleased(int index, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
            if (this.leftButton != null)
                this.leftButton.mouseReleased(mouseX, mouseY);
            if (this.rightButton != null)
                this.rightButton.mouseReleased(mouseX, mouseY);
        }
    }
}
