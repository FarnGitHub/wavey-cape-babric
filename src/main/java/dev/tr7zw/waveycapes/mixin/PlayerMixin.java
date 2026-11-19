package dev.tr7zw.waveycapes.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.waveycapes.CapeHolder;
import dev.tr7zw.waveycapes.sim.StickSimulation;

@Mixin(PlayerEntity.class)
public class PlayerMixin implements CapeHolder {

    @Unique
    private StickSimulation waveycape_stickSimulation = new StickSimulation();
    
    @Override
    public StickSimulation waveycape_getSimulation() {
        return waveycape_stickSimulation;
    }

    @Inject(method = "tickLiving", at = @At("HEAD"))
    private void moveCloakUpdate(CallbackInfo info) {
        simulate((PlayerEntity) (Object)this);
    }
    
}
