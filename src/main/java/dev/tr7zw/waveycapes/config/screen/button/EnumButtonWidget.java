package dev.tr7zw.waveycapes.config.screen.button;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.TranslationStorage;

public class EnumButtonWidget<T extends Enum<?>> extends ButtonWidget {
    private final String translationKey;
    private final Class<T> targetEnum;
    private final Supplier<T> current;
    private final Consumer<T> update;

    public EnumButtonWidget(String translationKey, Class<T> targetEnum, Supplier<T> current, Consumer<T> update) {
        super(0, 0, 0, 150, 20, "");
        this.translationKey = translationKey;
        this.targetEnum = targetEnum;
        this.current = current;
        this.update = update;
    }

    @Override
    public void render(Minecraft minecraft, int mouseX, int p_drawButton_3_) {
        this.text = TranslationStorage.getInstance().get(translationKey) + ": "
                + TranslationStorage.getInstance().get(translationKey + "." + current.get().name(), new Object[0]);
        super.render(minecraft, mouseX, p_drawButton_3_);
    }

    @Override
    public boolean isMouseOver(Minecraft minecraft, int mouseX, int mouseY) {
        if (super.isMouseOver(minecraft, mouseX, mouseY)) {
            update.accept(targetEnum.getEnumConstants()[(current.get().ordinal() + 1)
                    % targetEnum.getEnumConstants().length]);
            return true;
        }
        return false;
    }

}