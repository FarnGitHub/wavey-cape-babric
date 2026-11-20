package dev.tr7zw.waveycapes.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import dev.tr7zw.waveycapes.renderlayers.CustomCapeRenderLayer;

@Mixin(PlayerEntityRenderer.class)
public class PlayerRendererMixin {
    @Unique
    private CustomCapeRenderLayer waveyCapesLayer = new CustomCapeRenderLayer();


    @WrapOperation(method = "renderMore(Lnet/minecraft/entity/player/PlayerEntity;F)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;renderCape(F)V"))
    private void waveyCapes$redirectRenderCloak(BipedEntityModel instance, float scale, Operation<Void> original, @Local(argsOnly = true, type = PlayerEntity.class) PlayerEntity player, @Local(argsOnly = true, type = float.class) float tick) {
        if (waveyCapesLayer != null && player != null) {
            waveyCapesLayer.doRenderLayer(player, tick,true);
        }
    }

}
