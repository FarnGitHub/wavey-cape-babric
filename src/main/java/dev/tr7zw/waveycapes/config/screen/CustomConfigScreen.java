package dev.tr7zw.waveycapes.config.screen;

import dev.tr7zw.waveycapes.WaveyCapes;
import dev.tr7zw.waveycapes.config.Config;
import dev.tr7zw.waveycapes.config.enums.CapeMovement;
import dev.tr7zw.waveycapes.config.screen.button.BooleanButtonWidget;
import dev.tr7zw.waveycapes.config.screen.button.EnumButtonWidget;
import dev.tr7zw.waveycapes.config.screen.button.SliderButtonWidget;
import dev.tr7zw.waveycapes.config.screen.entry.ButtonEntryListWidget;
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
    protected String screenTitle;
    private ButtonEntryListWidget optionsRowList;

    public CustomConfigScreen(Screen lastScreen) {
        this.lastScreen = lastScreen;
    }

    @Override
    public void init() {
        this.screenTitle = TranslationStorage.getInstance().get("text.wc.title");
        this.buttons.clear();
        buttons.add(new ButtonWidget(200, this.width / 2 - 100, this.height - 27, TranslationStorage.getInstance().get("gui.done")));
        List<ButtonWidget> options = new ArrayList<>();
        Config config = WaveyCapes.config;
        options.add(getBooleanOption("text.wc.setting.capestyle", () -> config.blockyCape, v -> config.blockyCape = v));
        options.add(getBooleanOption("text.wc.setting.windmode", () -> config.wavyCape, v -> config.wavyCape = v));
        options.add(getEnumOption("text.wc.setting.capemovement", CapeMovement.class, () -> config.capeMovement, v -> config.capeMovement = v));
        options.add(getIntOption("text.wc.setting.gravity", 5, 32, () -> config.gravity, v -> config.gravity = v));
        options.add(getIntOption("text.wc.setting.heightMultiplier", 4, 16, () -> config.heightMultiplier, v -> config.heightMultiplier = v));
        this.optionsRowList = new ButtonEntryListWidget(this.minecraft, this.width, this.height, 32, this.height - 32, 25, options);
    }
    
    @Override
    protected void buttonClicked(ButtonWidget button) {
        if(button.id == 200) {
            this.minecraft.setScreen(this.lastScreen);
        }
    }

    public void removed() {
        WaveyCapes.config.save();
    }

    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        this.optionsRowList.mouseClicked(mouseX, mouseY, button);
    }

    protected void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        this.optionsRowList.mouseReleased(mouseX, mouseY, button);
    }

    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        this.optionsRowList.render(mouseX, mouseY, delta);
        this.drawCenteredTextWithShadow(this.textRenderer, this.screenTitle, this.width / 2, 5, 16777215);
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