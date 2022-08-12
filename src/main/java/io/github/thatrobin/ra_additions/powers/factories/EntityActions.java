package io.github.thatrobin.ra_additions.powers.factories;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.choice.Choice;
import io.github.thatrobin.ra_additions.choice.ChoiceLayers;
import io.github.thatrobin.ra_additions.component.ModComponents;
import io.github.thatrobin.ra_additions.networking.RAA_ModPackets;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;

public class EntityActions {

    public static void register() {
        register(new ActionFactory<>(RA_Additions.identifier("open_choice_screen"), new SerializableData()
                .add("choice_layer", SerializableDataTypes.IDENTIFIER),
                (data, entity) -> {
                    if(!entity.getEntityWorld().isClient()) {
                        if (entity instanceof PlayerEntity player) {
                            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
                            packet.writeBoolean(false);
                            packet.writeIdentifier(data.getId("choice_layer"));
                            ModComponents.CHOICE.get(player).setChoice(ChoiceLayers.getLayer(data.getId("choice_layer")), Choice.EMPTY);
                            ServerPlayNetworking.send((ServerPlayerEntity) player, RAA_ModPackets.OPEN_CHOICE_SCREEN, packet);
                        }
                    }
                }
        ));
    }

    private static void register(ActionFactory<Entity> actionFactory) {
        Registry.register(ApoliRegistries.ENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
    }
}
