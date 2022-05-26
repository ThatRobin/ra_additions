package io.github.thatrobin.ccpacksapoli.mixins;

import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.thatrobin.ccpacksapoli.choice.Choice;
import io.github.thatrobin.ccpacksapoli.choice.ChoiceLayers;
import io.github.thatrobin.ccpacksapoli.choice.ChoiceRegistry;
import io.github.thatrobin.ccpacksapoli.component.ChoiceComponent;
import io.github.thatrobin.ccpacksapoli.component.ModComponents;
import io.github.thatrobin.ccpacksapoli.networking.CCPacksModPackets;
import io.github.thatrobin.ccpacksapoli.util.UniversalPower;
import io.github.thatrobin.ccpacksapoli.util.UniversalPowerRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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

@SuppressWarnings("rawtypes")
@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Shadow public abstract List<ServerPlayerEntity> getPlayerList();

    @Inject(at = @At("TAIL"), method = "Lnet/minecraft/server/PlayerManager;onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V")
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
        ServerPlayNetworking.send(player, CCPacksModPackets.CHOICE_LIST, choiceListData);
        ServerPlayNetworking.send(player, CCPacksModPackets.LAYER_LIST, choiceLayerData);

        List<ServerPlayerEntity> playerList = getPlayerList();
        playerList.forEach(spe -> ModComponents.CHOICE.syncWith(spe, ComponentProvider.fromEntity(player)));
        ChoiceComponent.sync(player);
        component.sync();


        UniversalPowerRegistry.entries().forEach((identifierUniversalPowerEntry -> {
            Identifier id = identifierUniversalPowerEntry.getKey();
            UniversalPower up = identifierUniversalPowerEntry.getValue();
            if (up.entities.contains(player.getType())) {
                up.powerTypes.forEach(powerType -> {
                    PowerHolderComponent.KEY.get(player).addPower(powerType, id);
                });
            }
        }));

    }
}
