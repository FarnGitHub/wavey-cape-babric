package dev.tr7zw.waveycapes.config.screen.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.TranslationStorage;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class BooleanButtonWidget extends ButtonWidget {
    private final String translationKey;
    private final BooleanSupplier current;
    private final Consumer<Boolean> update;

    public BooleanButtonWidget(String translationKey, BooleanSupplier current, Consumer<Boolean> update) {
        super(0, 0, 0, 150, 20, "");
        this.translationKey = translationKey;
        this.current = current;
        this.update = update;
    }

    @Override
    public void render(Minecraft minecraft, int mouseX, int p_drawButton_3_) {
        this.text = TranslationStorage.getInstance().get(translationKey) + ": "
                + TranslationStorage.getInstance().get(translationKey + "." + current.getAsBoolean(), new Object[0]);
        super.render(minecraft, mouseX, p_drawButton_3_);
    }

    @Override
    public boolean isMouseOver(Minecraft minecraft, int mouseX, int mouseY) {
        if (super.isMouseOver(minecraft, mouseX, mouseY)) {
            update.accept(!current.getAsBoolean());
            return true;
        }
        return false;
    }

}