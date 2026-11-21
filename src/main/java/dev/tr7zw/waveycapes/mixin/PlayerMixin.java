package dev.tr7zw.waveycapes.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.waveycapes.impl.PlayerImpl;
import dev.tr7zw.waveycapes.sim.Simulation;

@Mixin(PlayerEntity.class)
public class PlayerMixin implements PlayerImpl {

    @Unique
    private Simulation waveycape_simulation;

    @Override
    public Simulation waveycape_getSimulation() {
        return waveycape_simulation;
    }

    @Override
    public void waveycape_setSimulation(Simulation sim) {
        this.waveycape_simulation = sim;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void waveycape$tickLiving(CallbackInfo info) {
        this.waveycape_checkInit();
        this.waveycape_tick();
    }
    
}
