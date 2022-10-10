package io.github.thatrobin.ra_additions.networking;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.choice.Choice;
import io.github.thatrobin.ra_additions.choice.ChoiceLayer;
import io.github.thatrobin.ra_additions.choice.ChoiceLayers;
import io.github.thatrobin.ra_additions.choice.ChoiceRegistry;
import io.github.thatrobin.ra_additions.client.RA_AdditionsClient;
import io.github.thatrobin.ra_additions.component.ChoiceComponent;
import io.github.thatrobin.ra_additions.component.ModComponents;
import io.github.thatrobin.ra_additions.screen.ChooseChoiceScreen;
import io.github.thatrobin.ra_additions.util.VariableIntPowerAccessor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RAA_ModPacketS2C {

    @Environment(EnvType.CLIENT)
    public static void register() {
        ClientLoginNetworking.registerGlobalReceiver(RAA_ModPackets.HANDSHAKE, RAA_ModPacketS2C::handleHandshake);
        ClientPlayConnectionEvents.INIT.register(((clientPlayNetworkHandler, minecraftClient) -> {
            ClientPlayNetworking.registerReceiver(RAA_ModPackets.CONFIRM_CHOICE, RAA_ModPacketS2C::chosenPowers);
            ClientPlayNetworking.registerReceiver(RAA_ModPackets.OPEN_CHOICE_SCREEN, RAA_ModPacketS2C::openChoiceScreen);
            ClientPlayNetworking.registerReceiver(RAA_ModPackets.CHOICE_LIST, RAA_ModPacketS2C::receiveChoiceList);
            ClientPlayNetworking.registerReceiver(RAA_ModPackets.LAYER_LIST, RAA_ModPacketS2C::receiveLayerList);
        }));
    }

    @Environment(EnvType.CLIENT)
    private static CompletableFuture<PacketByteBuf> handleHandshake(MinecraftClient minecraftClient, ClientLoginNetworkHandler clientLoginNetworkHandler, PacketByteBuf packetByteBuf, Consumer<GenericFutureListener<? extends Future<? super Void>>> genericFutureListenerConsumer) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(RA_Additions.SEMVER.length);
        for(int i = 0; i < RA_Additions.SEMVER.length; i++) {
            buf.writeInt(RA_Additions.SEMVER[i]);
        }
        RA_AdditionsClient.isServerRunningCCPacks = true;
        return CompletableFuture.completedFuture(buf);
    }

    @Environment(EnvType.CLIENT)
    private static void openChoiceScreen(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        boolean showDirtBackground = packetByteBuf.readBoolean();
        Identifier choiceLayer = packetByteBuf.readIdentifier();
        minecraftClient.execute(() -> {
            ArrayList<ChoiceLayer> layers = new ArrayList<>();
            if (minecraftClient.player != null) {
                ChoiceComponent component = ModComponents.CHOICE.get(minecraftClient.player);
                ChoiceLayer layer = ChoiceLayers.getLayer(choiceLayer);
                component.setChoice(layer, Choice.EMPTY);

                layers.add(layer);
                Collections.sort(layers);
                minecraftClient.setScreen(new ChooseChoiceScreen(layers, 0, showDirtBackground));
            }
        });
    }

    @Environment(EnvType.CLIENT)
    private static void receiveChoiceList(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        try {
            Identifier[] ids = new Identifier[packetByteBuf.readInt()];
            SerializableData.Instance[] choices = new SerializableData.Instance[ids.length];
            for(int i = 0; i < choices.length; i++) {
                ids[i] = Identifier.tryParse(packetByteBuf.readString());
                choices[i] = Choice.DATA.read(packetByteBuf);
            }
            minecraftClient.execute(() -> {
                RA_AdditionsClient.isServerRunningCCPacks = true;
                ChoiceRegistry.reset();
                for(int i = 0; i < ids.length; i++) {
                    ChoiceRegistry.register(ids[i], Choice.createFromData(ids[i], choices[i]));
                }
            });
        } catch (Exception e) {
            RA_Additions.LOGGER.error(e);
        }
    }

    @Environment(EnvType.CLIENT)
    private static void receiveLayerList(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        try {
            int layerCount = packetByteBuf.readInt();
            ChoiceLayer[] layers = new ChoiceLayer[layerCount];
            for(int i = 0; i < layerCount; i++) {
                layers[i] = ChoiceLayer.read(packetByteBuf);
            }
            minecraftClient.execute(() -> {
                ChoiceLayers.clear();
                for(int i = 0; i < layerCount; i++) {
                    ChoiceLayers.add(layers[i]);
                }
            });
        } catch (Exception e) {
            RA_Additions.LOGGER.error(e);
        }
    }

    @Environment(EnvType.CLIENT)
    private static void chosenPowers(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        ChoiceLayer layer = ChoiceLayers.getLayer(packetByteBuf.readIdentifier());
        Choice choice = ChoiceRegistry.get(packetByteBuf.readIdentifier());
        ActionFactory<Entity>.Instance action = choice.getAction();
        if(action != null) {
            action.accept(minecraftClient.player);
        }
        minecraftClient.execute(() -> {
            if (minecraftClient.player != null) {
                ChoiceComponent component = ModComponents.CHOICE.get(minecraftClient.player);
                component.setChoice(layer, choice);
            }

        });
    }

}
