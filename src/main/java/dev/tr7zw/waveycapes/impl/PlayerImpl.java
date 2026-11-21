package dev.tr7zw.waveycapes.impl;

import dev.tr7zw.waveycapes.WaveyCapes;

import dev.tr7zw.waveycapes.renderer.CapeRendererImpl;
import dev.tr7zw.waveycapes.sim.Simulation;
import dev.tr7zw.waveycapes.util.Mth;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public interface PlayerImpl {
    Simulation waveycape_getSimulation();

    void waveycape_setSimulation(Simulation sim);

    default void waveycape_updateSimulation() {
        Simulation simulation = waveycape_getSimulation();

        if (simulation == null || WaveyCapes.config.capeMovement.simulationClass != simulation.getClass()) {
            simulation = WaveyCapes.config.capeMovement.createSimulation();
            waveycape_setSimulation(simulation);
        }

        if (simulation != null && simulation.init(CapeRendererImpl.partCount)) {
            waveycape_getSimulation().applyMovement(1f, 1f, 0f);
            for (int why = 0; why < 5; why++) {
                waveycape_tick();
            }
        }

    }

    default void waveycape_tick() {
        PlayerEntity player = (PlayerEntity)this;
        Simulation simulation = waveycape_getSimulation();

        if(simulation == null || simulation.empty()) {
            return; // no cape, nothing to update
        }

        float d = (float)(player.capeX - player.x);
        float m = (float)(player.capeZ - player.z);
        float n = player.lastBodyYaw + player.bodyYaw - player.lastBodyYaw;
        float o = MathHelper.sin(n * 0.017453292F);
        float p = -MathHelper.cos(n * 0.017453292F);
        float heightMul = WaveyCapes.config.heightMultiplier;
        if (player.isInFluid(Material.WATER)) {
            heightMul *= 2; // let the cape have more drag than the player underwater
        }
        // gives the cape a small swing when jumping/falling to not clip with itself/simulate some air getting under it
        float fallHack = Mth.clamp((float)(player.prevY - player.y)*heightMul, 0f, 1f);
        if (player.isInFluid(Material.WATER)) {
            simulation.setGravity(WaveyCapes.config.gravity / 10f);
        } else {
            simulation.setGravity(WaveyCapes.config.gravity);
        }
        float yaw = (float) Math.toRadians(-player.yaw);
        float strave = MathHelper.cos(yaw) * (float) (player.x - player.prevX) - MathHelper.sin(yaw) * (float) (player.z - player.prevZ);
        float changeX = (d * o + m * p) + fallHack;
        float changeY = (float) ((player.y - player.prevY)*heightMul + (player.isSneaking() ? -4 : 0));
        float changeZ = -strave * 2;
        simulation.setGravityDirection(0f, -1f, 0f);
        simulation.applyMovement(changeX, changeY, changeZ);
        simulation.simulate();
    }
    
}
