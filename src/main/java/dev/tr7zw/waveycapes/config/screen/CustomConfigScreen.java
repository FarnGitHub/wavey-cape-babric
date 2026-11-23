package dev.tr7zw.waveycapes.config.screen;

import dev.tr7zw.waveycapes.WaveyCapes;
import dev.tr7zw.waveycapes.config.Config;
import dev.tr7zw.waveycapes.config.enums.CapeMovement;
import dev.tr7zw.waveycapes.config.screen.button.BooleanButtonWidget;
import dev.tr7zw.waveycapes.config.screen.button.EnumButtonWidget;
import dev.tr7zw.waveycapes.config.screen.button.SliderButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.TranslationStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class CustomConfigScreen extends Screen {

    protected final Screen lastScreen;

    public CustomConfigScreen(Screen lastScreen) {
        this.lastScreen = lastScreen;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        this.buttons.clear();
        this.buttons.add(new ButtonWidget(200, this.width / 2 - 100, this.height - 27, TranslationStorage.getInstance().get("gui.done")));
        List<ButtonWidget> options = new ArrayList<>();
        Config config = WaveyCapes.config;
        options.add(getBooleanOption(
                "waveycapes.capestyle",
                () -> config.blockyCape,
                v -> config.blockyCape = v)
        );
        options.add(getBooleanOption(
                "waveycapes.windmode",
                () -> config.wavyCape,
                v -> config.wavyCape = v)
        );
        options.add(getEnumOption(
                "waveycapes.capemovement",
                CapeMovement.class,
                () -> config.capeMovement,
                v -> config.capeMovement = v)
        );
        options.add(getIntOption(
                "waveycapes.gravity",
                5, 32,
                () -> config.gravity,
                v -> config.gravity = v)
        );
        options.add(getIntOption(
                "waveycapes.heightMultiplier",
                4, 16,
                () -> config.heightMultiplier,
                v -> config.heightMultiplier = v)
        );
        for(int index = 0; index < options.size(); ++index) {
            ButtonWidget option = options.get(index);
            option.x = this.width / 2 - 155 + index % 2 * 160;
            option.y = this.height / 6 + 24 * (index >> 1);
        }
        this.buttons.addAll(options);
    }
    
    @Override
    protected void buttonClicked(ButtonWidget button) {
        if(button.id == 200) {
            this.minecraft.setScreen(this.lastScreen);
        }
    }

    public void removed() {
        WaveyCapes.writeConfig();
    }

    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
    }

    protected void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
    }

    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        this.drawCenteredTextWithShadow(this.textRenderer, WaveyCapes.translate("waveycapes.title"), this.width / 2, 5, 16777215);
        super.render(mouseX, mouseY, delta);
    }

    public ButtonWidget getBooleanOption(String translationKey, BooleanSupplier current, Consumer<Boolean> update) {
        return new BooleanButtonWidget(translationKey, current, update);
    }

    public ButtonWidget getIntOption(String translationKey, float min, float max, Supplier<Integer> current, Consumer<Integer> update) {
        return new SliderButtonWidget(translationKey, min, max, 1, () -> (float)current.get(), (d) -> update.accept(d.intValue()));
    }

    public <T extends Enum<?>> ButtonWidget getEnumOption(String translationKey, Class<T> targetEnum, Supplier<T> current, Consumer<T> update) {
        return new EnumButtonWidget<>(translationKey, targetEnum, current, update);
    }

}