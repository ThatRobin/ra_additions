package io.github.thatrobin.ra_additions.powers.factories;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.docky.utils.SectionTitleManager;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.choice.Choice;
import io.github.thatrobin.ra_additions.choice.ChoiceLayers;
import io.github.thatrobin.ra_additions.component.ModComponents;
import io.github.thatrobin.ra_additions.networking.RAA_ModPackets;
import io.github.thatrobin.ra_additions.util.*;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class EntityActions {

    public static void register(String label) {
        SectionTitleManager.put("Action Types", "entity_action");

        register(new ActionFactory<>(RA_Additions.identifier("open_choice_screen"), new SerializableDataExt(label)
                .add("choice_layer", "The Identifier of the choice layer that the action will open.", SerializableDataTypes.IDENTIFIER),
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
        register(new ActionFactory<>(RA_Additions.identifier("execute_action"), new SerializableDataExt(label)
                .add("entity_action", "The Identifier of the tag or action file to be executed", SerializableDataTypes.STRING),
                (data, entity) -> {
                    String idStr = data.getString("entity_action");
                    if(idStr.startsWith("#")) {
                        Identifier id = Identifier.tryParse(idStr.substring(1));
                        Collection<ActionType> actions = EntityActionTagManager.ACTION_TAG_LOADER.getTag(id);
                        for (ActionType action : actions) {
                            action.getAction().accept(entity);
                        }
                    } else {
                        Identifier id = Identifier.tryParse(idStr);
                        ActionFactory<Entity>.Instance action =  EntityActionRegistry.get(id).getAction();
                        action.accept(entity);
                    }
                }));
    }


    private static void register(ActionFactory<Entity> factory) {
        Registry.register(ApoliRegistries.ENTITY_ACTION, factory.getSerializerId(), factory);
    }
}
