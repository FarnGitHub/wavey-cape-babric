package dev.tr7zw.waveycapes.config;

import dev.tr7zw.waveycapes.config.enums.CapeMovement;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class Config {

    @ConfigEntry(name="Wavy Cape", description = "Add extra movement to the cape.")
    public Boolean wavyCape = false;

    @ConfigEntry(name="Blocky Style Cape", description = "Change the render from a smoothly bending cape to a blocky style")
    public Boolean blockyCape = false;

    @ConfigEntry(name="Cape Movement", description = "Change the way the cape behaves.")
    public CapeMovement capeMovement = CapeMovement.BASIC_SIMULATION;

    @ConfigEntry(name="Gravity", description = "Change the amount of gravity applied to the cape.", maxValue = Integer.MAX_VALUE)
    public Integer gravity = 25;

    @ConfigEntry(name="Height Multiplier", description = "Change how extreme the cape reacts to Y movement.", maxValue = Integer.MAX_VALUE)
    public Integer heightMultiplier = 6;
}
