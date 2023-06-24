package io.github.thatrobin.ra_additions.networking;

import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.client.RA_AdditionsClient;
import io.github.thatrobin.ra_additions.util.KeybindRegistry;
import io.github.thatrobin.ra_additions.util.KeybindingData;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class RAA_ModPacketS2C {

    @Environment(EnvType.CLIENT)
    public static void register() {
        ClientLoginNetworking.registerGlobalReceiver(RAA_ModPackets.HANDSHAKE, RAA_ModPacketS2C::handleHandshake);
        ClientPlayConnectionEvents.INIT.register((clientPlayNetworkHandler, minecraftClient) -> {
            ClientPlayNetworking.registerReceiver(RAA_ModPackets.SEND_KEYBINDS, RAA_ModPacketS2C::sendKeyBinds);
        });
    }

    @Environment(EnvType.CLIENT)
    private static CompletableFuture<PacketByteBuf> handleHandshake(MinecraftClient minecraftClient, ClientLoginNetworkHandler clientLoginNetworkHandler, PacketByteBuf packetByteBuf, Consumer<GenericFutureListener<? extends Future<? super Void>>> genericFutureListenerConsumer) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(RA_Additions.SEMVER.length);
        for(int i = 0; i < RA_Additions.SEMVER.length; i++) {
            buf.writeInt(RA_Additions.SEMVER[i]);
        }
        RA_AdditionsClient.isServerRunningRAA = true;
        return CompletableFuture.completedFuture(buf);
    }

    @Environment(EnvType.CLIENT)
    private static void sendKeyBinds(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        try {
            int amount = packetByteBuf.readInt();
            Map<Identifier, KeybindingData> map = new HashMap<>();
            for(int i = 0; i < amount; i++) {
                Identifier id = Identifier.tryParse(packetByteBuf.readString());
                KeybindingData key = KeybindingData.fromBuffer(packetByteBuf);
                map.put(id, key);
            }
            minecraftClient.execute(() -> {
                KeybindRegistry.reset();
                map.forEach(((identifier, keyBinding) -> {
                    if (!KeybindRegistry.contains(identifier)) {
                        KeybindRegistry.registerClient(identifier, keyBinding);
                    }
                }));
                minecraftClient.options.allKeys = KeyBindingRegistryImpl.process(minecraftClient.options.allKeys);
            });
            RA_Additions.LOGGER.info("Finished loading client KeyBinding from data files. Registry contains " + KeybindRegistry.size() + " KeyBinding files.");
        } catch (Exception e) {
            RA_Additions.LOGGER.error(e.getStackTrace());
        }

    }

}
