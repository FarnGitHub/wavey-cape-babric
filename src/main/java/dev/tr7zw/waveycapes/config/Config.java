package dev.tr7zw.waveycapes.config;

import dev.tr7zw.waveycapes.config.enums.CapeMovement;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class Config {

    @ConfigEntry(name="Windy Cape")
    public Boolean windyCape = false;

    @ConfigEntry(name="Blocky Cape")
    public Boolean blockyCape = false;

    @ConfigEntry(name="Movement")
    public CapeMovement capeMovement = CapeMovement.BASIC_SIMULATION;

    @ConfigEntry(name="Gravity", maxValue = Integer.MAX_VALUE)
    public Integer gravity = 25;

    @ConfigEntry(name="Height Multiplier", maxValue = Integer.MAX_VALUE)
    public Integer heightMultiplier = 6;
}
