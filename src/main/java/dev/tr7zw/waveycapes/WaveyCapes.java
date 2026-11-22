package dev.tr7zw.waveycapes;

import dev.tr7zw.waveycapes.config.Config;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.language.TranslationStorage;

import java.io.IOException;

public class WaveyCapes{

    public static Config config = new Config();

    static {
        if(!FabricLoader.getInstance().isModLoaded("stationapi")){
            try {
                TranslationStorage.getInstance().translations.load(WaveyCapes.class.getResourceAsStream("/assets/waveycapes/stationapi/lang/en_US.lang"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
