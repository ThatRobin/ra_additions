package io.github.thatrobin.ra_additions_choices.networking;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_choices.choice.Choice;
import io.github.thatrobin.ra_additions_choices.choice.ChoiceLayer;
import io.github.thatrobin.ra_additions_choices.choice.ChoiceLayers;
import io.github.thatrobin.ra_additions_choices.choice.ChoiceRegistry;
import io.github.thatrobin.ra_additions_choices.component.ChoiceComponent;
import io.github.thatrobin.ra_additions_choices.component.ModComponents;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class RAAC_ModPacketC2S {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(RAAC_ModPackets.CHOOSE_CHOICE, RAAC_ModPacketC2S::choosePowers);
    }

    private static void choosePowers(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        String originId = packetByteBuf.readString(32767);
        String layerId = packetByteBuf.readString(32767);
        minecraftServer.execute(() -> {
            ChoiceComponent component = ModComponents.CHOICE.get(playerEntity);
            ChoiceLayer layer = ChoiceLayers.getLayer(Identifier.tryParse(layerId));
            if(!component.hasAllChoices() && !component.hasChoice(layer)) {
                Identifier id = Identifier.tryParse(originId);
                if(id != null) {
                    Choice choice = ChoiceRegistry.get(id);
                    if(layer.contains(choice)) {
                        component.setChoice(layer, choice);
                        component.checkAutoChoosingLayers(playerEntity);
                        component.sync();
                        RA_Additions.LOGGER.info("Player " + playerEntity.getDisplayName().getString() + " selected Choice: " + id + ", for layer: " + layerId);
                    } else {
                        RA_Additions.LOGGER.info("Player " + playerEntity.getDisplayName().getString() + " tried to select unchoosable Choice for layer " + layerId + ": " + id + ".");
                        component.setChoice(layer, Choice.EMPTY);
                    }
                    confirmChoice(playerEntity, layer, component.getChoice(layer));
                    component.sync();
                } else {
                    RA_Additions.LOGGER.warn("Player " + playerEntity.getDisplayName().getString() + " selected unknown Choice");
                }
            } else {
                component.sync();
                RA_Additions.LOGGER.warn("Player " + playerEntity.getDisplayName().getString() + " tried to select Choice for layer " + layerId + " while having one already.");
            }
        });
    }

    private static void confirmChoice(ServerPlayerEntity player, ChoiceLayer layer, Choice choice) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(layer.getIdentifier());
        buf.writeIdentifier(choice.getIdentifier());
        ActionFactory<Entity>.Instance action = choice.getAction();
        if(action != null) {
            action.accept(player);
        }
        ServerPlayNetworking.send(player, RAAC_ModPackets.CONFIRM_CHOICE, buf);
    }

}
