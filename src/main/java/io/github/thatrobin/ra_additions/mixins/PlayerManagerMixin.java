package io.github.thatrobin.ra_additions.mixins;

import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ra_additions.choice.Choice;
import io.github.thatrobin.ra_additions.choice.ChoiceLayers;
import io.github.thatrobin.ra_additions.choice.ChoiceRegistry;
import io.github.thatrobin.ra_additions.component.ChoiceComponent;
import io.github.thatrobin.ra_additions.component.ModComponents;
import io.github.thatrobin.ra_additions.networking.RAA_ModPackets;
import io.github.thatrobin.ra_additions.util.KeybindRegistry;
import io.github.thatrobin.ra_additions.util.KeybindingData;
import io.github.thatrobin.ra_additions.util.UniversalPower;
import io.github.thatrobin.ra_additions.util.UniversalPowerRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Shadow public abstract List<ServerPlayerEntity> getPlayerList();

    @Inject(at = @At("TAIL"), method = "onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V")
    private void openChoiceGui(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        ChoiceComponent component = ModComponents.CHOICE.get(player);

        PacketByteBuf choiceListData = new PacketByteBuf(Unpooled.buffer());
        choiceListData.writeInt(ChoiceRegistry.size() - 1);
        ChoiceRegistry.entries().forEach((entry) -> {
            if (entry.getValue() != Choice.EMPTY) {
                choiceListData.writeIdentifier(entry.getKey());
                entry.getValue().write(choiceListData);
            }
        });

        PacketByteBuf choiceLayerData = new PacketByteBuf(Unpooled.buffer());
        choiceLayerData.writeInt(ChoiceLayers.size());
        ChoiceLayers.getLayers().forEach((layer) -> {
            layer.write(choiceLayerData);
            if (layer.isEnabled()) {
                if (!component.hasChoice(layer)) {
                    component.setChoice(layer, Choice.EMPTY);
                }
            }
        });

        PacketByteBuf keybindData = new PacketByteBuf(Unpooled.buffer());
        keybindData.writeInt(KeybindRegistry.size());
        KeybindRegistry.entries().forEach((bindingEntry) -> {
            Identifier identifier = bindingEntry.getKey();
            KeybindingData data = bindingEntry.getValue();
            keybindData.writeString(identifier.toString());
            data.toBuffer(keybindData, identifier);
        });

        ServerPlayNetworking.send(player, RAA_ModPackets.SEND_KEYBINDS, keybindData);
        ServerPlayNetworking.send(player, RAA_ModPackets.CHOICE_LIST, choiceListData);
        ServerPlayNetworking.send(player, RAA_ModPackets.LAYER_LIST, choiceLayerData);

        List<ServerPlayerEntity> playerList = getPlayerList();
        playerList.forEach(spe -> ModComponents.CHOICE.syncWith(spe, (ComponentProvider)player));
        ChoiceComponent.sync(player);
        component.sync();

        UniversalPowerRegistry.entries().forEach((identifierUniversalPowerEntry -> {
            Identifier id = identifierUniversalPowerEntry.getKey();
            UniversalPower up = identifierUniversalPowerEntry.getValue();
            if (up.entities.contains(player.getType())) {
                up.powerTypes.forEach(powerType -> PowerHolderComponent.KEY.get(player).addPower(powerType, id));
            }
        }));

    }
}
