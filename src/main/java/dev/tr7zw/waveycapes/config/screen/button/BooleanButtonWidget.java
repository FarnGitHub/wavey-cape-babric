package dev.tr7zw.waveycapes.config.screen.button;

import dev.tr7zw.waveycapes.WaveyCapes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class BooleanButtonWidget extends ButtonWidget implements OptionWidget {
    private final String translationKey;
    private final BooleanSupplier current;
    private final Consumer<Boolean> update;

    public BooleanButtonWidget(String translationKey, BooleanSupplier current, Consumer<Boolean> update) {
        super(0, 0, 0, 150, 20, "");
        this.translationKey = translationKey;
        this.current = current;
        this.update = update;
        this.text = updateDisplayText();
    }

    @Override
    public boolean isMouseOver(Minecraft minecraft, int mouseX, int mouseY) {
        if (super.isMouseOver(minecraft, mouseX, mouseY)) {
            this.update.accept(!this.current.getAsBoolean());
            this.text = updateDisplayText();
            return true;
        }
        return false;
    }

    @Override
    public String updateDisplayText() {
        return WaveyCapes.translate(translationKey) + ": " + WaveyCapes.translate(translationKey + "." + current.getAsBoolean());
    }

}