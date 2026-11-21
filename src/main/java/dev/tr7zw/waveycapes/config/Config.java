package dev.tr7zw.waveycapes.config;

import dev.tr7zw.waveycapes.config.enums.CapeMovement;
import dev.tr7zw.waveycapes.config.enums.CapeStyle;
import dev.tr7zw.waveycapes.config.enums.WindMode;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class Config {

    @ConfigEntry(name="Wind Mode")
    public WindMode windMode = WindMode.NONE;

    @ConfigEntry(name="Style")
    public CapeStyle capeStyle = CapeStyle.SMOOTH;

    @ConfigEntry(name="Movement")
    public CapeMovement capeMovement = CapeMovement.BASIC_SIMULATION;

    @ConfigEntry(name="Gravity", maxValue = Integer.MAX_VALUE)
    public Integer gravity = 25;

    @ConfigEntry(name="Height Multiplier", maxValue = Integer.MAX_VALUE)
    public Integer heightMultiplier = 6;
}
