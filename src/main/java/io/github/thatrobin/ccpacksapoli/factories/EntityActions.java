package io.github.thatrobin.ccpacksapoli.factories;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ccpacksapoli.CCPacksApoli;
import io.github.thatrobin.ccpacksapoli.choice.Choice;
import io.github.thatrobin.ccpacksapoli.choice.ChoiceLayers;
import io.github.thatrobin.ccpacksapoli.component.ModComponents;
import io.github.thatrobin.ccpacksapoli.networking.CCPacksModPackets;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class EntityActions {

    public static void register() {
        register(new ActionFactory<>(CCPacksApoli.identifier("open_choice_screen"), new SerializableData()
                .add("choice_layer", SerializableDataTypes.IDENTIFIER),
                (data, entity) -> {
                    if(!entity.getEntityWorld().isClient()) {
                        if (entity instanceof PlayerEntity player) {
                            PacketByteBuf data2 = new PacketByteBuf(Unpooled.buffer());
                            data2.writeBoolean(false);
                            data2.writeIdentifier(data.getId("choice_layer"));
                            ModComponents.CHOICE.get(player).setChoice(ChoiceLayers.getLayer(data.getId("choice_layer")), Choice.EMPTY);
                            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, CCPacksModPackets.OPEN_CHOICE_SCREEN, data2);
                        }
                    }
                }
        ));

    }

    private static void register(ActionFactory<Entity> actionFactory) {
        Registry.register(ApoliRegistries.ENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
    }
}
