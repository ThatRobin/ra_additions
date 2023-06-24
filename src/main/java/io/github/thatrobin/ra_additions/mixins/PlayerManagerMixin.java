package io.github.thatrobin.ra_additions.mixins;

import io.github.thatrobin.ra_additions.networking.RAA_ModPackets;
import io.github.thatrobin.ra_additions.util.KeybindRegistry;
import io.github.thatrobin.ra_additions.util.KeybindingData;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Inject(at = @At("TAIL"), method = "onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V")
    private void openChoiceGui(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        PacketByteBuf keybindData = new PacketByteBuf(Unpooled.buffer());
        keybindData.writeInt(KeybindRegistry.size());
        KeybindRegistry.entries().forEach((bindingEntry) -> {
            Identifier identifier = bindingEntry.getKey();
            KeybindingData data = bindingEntry.getValue();
            keybindData.writeString(identifier.toString());
            data.toBuffer(keybindData, identifier);
        });

        ServerPlayNetworking.send(player, RAA_ModPackets.SEND_KEYBINDS, keybindData);
    }
}
