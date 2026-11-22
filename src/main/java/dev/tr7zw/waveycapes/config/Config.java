package dev.tr7zw.waveycapes.config;

import dev.tr7zw.waveycapes.config.enums.CapeMovement;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

public class Config {

    public static final File file = FabricLoader.getInstance().getConfigDir().resolve("waveycapes.properties").toFile();

    public boolean wavyCape = false;
    public boolean blockyCape = false;
    public CapeMovement capeMovement = CapeMovement.BASIC_SIMULATION_3D;
    public int gravity = 25;
    public int heightMultiplier = 6;

    public void save() {
        try(PrintWriter writer = new PrintWriter(file)) {
            writer.println("wavyCape=" + wavyCape);
            writer.println("blockyCape=" + blockyCape);
            writer.println("capeMovement=" + capeMovement);
            writer.println("gravity=" + gravity);
            writer.println("heightMultiplier=" + heightMultiplier);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Config() {
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] splited = line.split("=");
                switch(splited[0]) {
                    case "wavyCape": {
                        wavyCape = splited[1].equals("true");
                        break;
                    }
                    case "blockyCape": {
                        blockyCape = splited[1].equals("true");
                        break;
                    }
                    case "capeMovement": {
                        capeMovement = CapeMovement.valueOf(splited[1].toUpperCase());
                        break;
                    }
                    case "gravity": {
                        gravity = Integer.parseInt(splited[1]);
                        break;
                    }
                    case "heightMultiplier": {
                        heightMultiplier = Integer.parseInt(splited[1]);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
