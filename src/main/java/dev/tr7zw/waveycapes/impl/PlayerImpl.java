package dev.tr7zw.waveycapes.impl;

import dev.tr7zw.waveycapes.WaveyCapes;
import dev.tr7zw.waveycapes.sim.Simulation;
import dev.tr7zw.waveycapes.util.Mth;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerImpl {
    Simulation waveycape_getSimulation();

    default void waveycape_simulate(PlayerEntity player) {
        Simulation simulation = waveycape_getSimulation();
        if(simulation.points.isEmpty()) {
            return; // no cape, nothing to update
        }
        //PlayerEntity player = (PlayerEntity)this;
        simulation.points.get(0).prevPosition.copy(simulation.points.get(0).position);
        float d = (float)(player.capeX - player.x);
        float m = (float)(player.capeZ- player.z);
        float n = player.lastBodyYaw + player.bodyYaw - player.lastBodyYaw;
        float o = (float)Math.sin(n * 0.017453292F);
        float p = (float)-Math.cos(n * 0.017453292F);
        float heightMul = WaveyCapes.config.heightMultiplier;
        // gives the cape a small swing when jumping/falling to not clip with itself/simulate some air getting under it
        float fallHack = Mth.clamp((float)(simulation.points.get(0).position.y - (player.y*heightMul)), 0f, 1f);
        simulation.points.get(0).position.x += (d * o + m * p) + fallHack;
        simulation.points.get(0).position.y = (float) (player.y*heightMul + (player.isSneaking() ? -4 : 0));
        simulation.simulate();
    }
    
}
