package io.github.thatrobin.ccpacksapoli.mixins;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ccpacksapoli.power.BindPower;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Final public GameOptions options;

    @Shadow @Nullable public ClientPlayerEntity player;

    @Redirect(
            method = "handleInputEvents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 6)
    )
    private boolean handleInputEvents(KeyBinding instance) {
        for (BindPower bindPower : PowerHolderComponent.getPowers(this.player, BindPower.class)) {
            if(bindPower.doesApply(this.player.getMainHandStack()) || bindPower.doesApply(this.player.getOffHandStack())) {
                return false;
            }
        }
        return this.options.swapHandsKey.wasPressed();
    }
}
