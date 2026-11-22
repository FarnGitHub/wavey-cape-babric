package dev.tr7zw.waveycapes.config.screen.button;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.TranslationStorage;
import org.lwjgl.opengl.GL11;

public class SliderButtonWidget extends ButtonWidget {
    private float value;
    private final String translationKey;
    public boolean dragging;
    private final float min,max,steps;
    private final Supplier<Float> current;
    private final Consumer<Float> update;


    public SliderButtonWidget(String translationKey, float min, float max, float steps,
                              Supplier<Float> current, Consumer<Float> update) {
        super(0, 0, 0, 150, 20, "");
        this.translationKey = translationKey;
        this.min = min;
        this.max = max;
        this.steps = steps;
        this.current = current;
        this.update = update;
        this.value = ((current.get()-min) / (max-min));
        this.text = TranslationStorage.getInstance().get(translationKey) + ": " + current.get();
    }

    protected int getYImage(boolean hovered) {
        return 0;
    }

    protected void renderBackground(Minecraft minecraft, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.dragging) {
                this.onDrag(mouseX);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexture(this.x + (int)(this.value * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
            this.drawTexture(this.x + (int)(this.value * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
        }
    }

    public boolean isMouseOver(Minecraft mc, int mouseX, int mouseY) {
        if (super.isMouseOver(mc, mouseX, mouseY)) {
            this.onDrag(mouseX);
            this.dragging = true;
            return true;
        }
        return false;
    }

    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }

    private void onDrag(int mouseX) {
        this.value = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
        if (this.value < 0.0F) {
            this.value = 0.0F;
        }

        if (this.value > 1.0F) {
            this.value = 1.0F;
        }

        float actualValue = min + (value * (max-min));
        actualValue = (int)(actualValue/steps);
        actualValue *= steps;
        update.accept(actualValue);
        this.text = TranslationStorage.getInstance().get(translationKey) + ": " + current.get();
    }
}
