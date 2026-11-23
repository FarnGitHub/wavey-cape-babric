package dev.tr7zw.waveycapes.config.screen.button;

import java.util.function.Consumer;
import java.util.function.Supplier;

import dev.tr7zw.waveycapes.WaveyCapes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;

public class EnumButtonWidget<T extends Enum<?>> extends ButtonWidget implements OptionWidget{
    private final String translationKey;
    private final Class<T> tEnum;
    private final Supplier<T> current;
    private final Consumer<T> update;

    public EnumButtonWidget(String translationKey, Class<T> tEnum, Supplier<T> current, Consumer<T> update) {
        super(0, 0, 0, 150, 20, "");
        this.translationKey = translationKey;
        this.tEnum = tEnum;
        this.current = current;
        this.update = update;
        this.text = updateDisplayText();
    }

    @Override
    public void render(Minecraft minecraft, int mouseX, int p_drawButton_3_) {
        super.render(minecraft, mouseX, p_drawButton_3_);
    }

    @Override
    public boolean isMouseOver(Minecraft minecraft, int mouseX, int mouseY) {
        if (super.isMouseOver(minecraft, mouseX, mouseY)) {
            T[] values = this.tEnum.getEnumConstants();
            int next = current.get().ordinal() + 1 % values.length;
            this.update.accept(values[next]);
            this.text = updateDisplayText();
            return true;
        }
        return false;
    }

    @Override
    public String updateDisplayText() {
        return WaveyCapes.translate(translationKey) + ": " + WaveyCapes.translate(translationKey + "." + current.get().name());
    }
}