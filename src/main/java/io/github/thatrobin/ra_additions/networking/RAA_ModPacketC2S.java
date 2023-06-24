package io.github.thatrobin.ra_additions.networking;

import io.github.thatrobin.ra_additions.RA_Additions;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;

public class RAA_ModPacketC2S {

    public static void register() {
        ServerLoginConnectionEvents.QUERY_START.register(RAA_ModPacketC2S::handshake);
        ServerLoginNetworking.registerGlobalReceiver(RAA_ModPackets.HANDSHAKE, RAA_ModPacketC2S::handleHandshakeReply);
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

    private static void handshake(ServerLoginNetworkHandler serverLoginNetworkHandler, MinecraftServer minecraftServer, PacketSender packetSender, ServerLoginNetworking.LoginSynchronizer loginSynchronizer) {
        packetSender.sendPacket(RAA_ModPackets.HANDSHAKE, PacketByteBufs.empty());
    }

}
