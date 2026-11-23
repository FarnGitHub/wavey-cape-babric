package dev.tr7zw.waveycapes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.tr7zw.waveycapes.config.Config;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.language.TranslationStorage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class WaveyCapes {

    public static Config config;
    private static final Path settingsFile = FabricLoader.getInstance().getConfigDir().resolve("waveycapes.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    static {
        if(!FabricLoader.getInstance().isModLoaded("stationapi")) {
            try {
                TranslationStorage.getInstance().translations.load(WaveyCapes.class.getResourceAsStream("/assets/waveycapes/stationapi/lang/en_US.lang"));
            } catch (IOException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        }
        if (Files.exists(settingsFile)) {
            try {
                config = gson.fromJson(Files.readString(settingsFile), Config.class);
            } catch (Exception ex) {
                System.out.println("Error while loading config! Creating a new one!");
                //noinspection CallToPrintStackTrace
                ex.printStackTrace();
            }
        }

        if (config == null) {
            config = new Config();
            writeConfig();
        }
    }

    public static void writeConfig() {
        try {
            Files.deleteIfExists(settingsFile);
            Files.writeString(settingsFile, gson.toJson(config));
        } catch (IOException e1) {
            //noinspection CallToPrintStackTrace
            e1.printStackTrace();
        }
    }

    public static String translate(String key) {
        return TranslationStorage.getInstance().get(key);
    }
}
