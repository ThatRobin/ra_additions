package io.github.thatrobin.ra_additions.networking;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.choice.Choice;
import io.github.thatrobin.ra_additions.choice.ChoiceLayer;
import io.github.thatrobin.ra_additions.choice.ChoiceLayers;
import io.github.thatrobin.ra_additions.choice.ChoiceRegistry;
import io.github.thatrobin.ra_additions.component.ChoiceComponent;
import io.github.thatrobin.ra_additions.component.ModComponents;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RAA_ModPacketC2S {

    public static void register() {
        ServerLoginConnectionEvents.QUERY_START.register(RAA_ModPacketC2S::handshake);
        ServerLoginNetworking.registerGlobalReceiver(RAA_ModPackets.HANDSHAKE, RAA_ModPacketC2S::handleHandshakeReply);
        ServerPlayNetworking.registerGlobalReceiver(RAA_ModPackets.CHOOSE_CHOICE, RAA_ModPacketC2S::choosePowers);
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

    private static void handleHandshakeReply(MinecraftServer minecraftServer, ServerLoginNetworkHandler serverLoginNetworkHandler, boolean understood, PacketByteBuf packetByteBuf, ServerLoginNetworking.LoginSynchronizer loginSynchronizer, PacketSender packetSender) {
        if (understood) {
            int clientSemVerLength = packetByteBuf.readInt();
            int[] clientSemVer = new int[clientSemVerLength];
            boolean mismatch = clientSemVerLength != RA_Additions.SEMVER.length;
            for(int i = 0; i < clientSemVerLength; i++) {
                clientSemVer[i] = packetByteBuf.readInt();
                if(i < clientSemVerLength - 1 && clientSemVer[i] != RA_Additions.SEMVER[i]) {
                    mismatch = true;
                }
            }
            if(mismatch) {
                StringBuilder clientVersionString = new StringBuilder();
                for(int i = 0; i < clientSemVerLength; i++) {
                    clientVersionString.append(clientSemVer[i]);
                    if(i < clientSemVerLength - 1) {
                        clientVersionString.append(".");
                    }
                }
                serverLoginNetworkHandler.disconnect(Text.translatable("ra_additions.choices.gui.version_mismatch", RA_Additions.VERSION, clientVersionString));
            }
        } else {
            serverLoginNetworkHandler.disconnect(Text.literal("This server requires you to install Robin's Apoli Additions (v" + RA_Additions.VERSION + ") to play."));
        }
    }

    private static void confirmChoice(ServerPlayerEntity player, ChoiceLayer layer, Choice choice) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(layer.getIdentifier());
        buf.writeIdentifier(choice.getIdentifier());
        ActionFactory<Entity>.Instance action = choice.getAction();
        if(action != null) {
            action.accept(player);
        }
        ServerPlayNetworking.send(player, RAA_ModPackets.CONFIRM_CHOICE, buf);
    }

    private static void handshake(ServerLoginNetworkHandler serverLoginNetworkHandler, MinecraftServer minecraftServer, PacketSender packetSender, ServerLoginNetworking.LoginSynchronizer loginSynchronizer) {
        packetSender.sendPacket(RAA_ModPackets.HANDSHAKE, PacketByteBufs.empty());
    }

}
