package io.github.thatrobin.ra_additions_choices.factories;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_choices.choice.Choice;
import io.github.thatrobin.ra_additions_choices.choice.ChoiceLayers;
import io.github.thatrobin.ra_additions_choices.component.ModComponents;
import io.github.thatrobin.ra_additions_choices.networking.RAAC_ModPackets;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;

public class EntityActions {

    public static void register() {
        register(new ActionFactory<>(RA_Additions.identifier("open_choice_screen"), new SerializableDataExt()
                .add("choice_layer", "The Identifier of the choice layer that the action will open.", SerializableDataTypes.IDENTIFIER),
                (data, entity) -> {
                    if (!entity.getEntityWorld().isClient()) {
                        if (entity instanceof PlayerEntity player) {
                            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
                            packet.writeBoolean(false);
                            packet.writeIdentifier(data.getId("choice_layer"));
                            ModComponents.CHOICE.get(player).setChoice(ChoiceLayers.getLayer(data.getId("choice_layer")), Choice.EMPTY);
                            ServerPlayNetworking.send((ServerPlayerEntity) player, RAAC_ModPackets.OPEN_CHOICE_SCREEN, packet);
                        }
                    }
                }
        ), "Opens the specified choice screen for the player it is executed as.");
    }

    private static void register(ActionFactory<Entity> factory, String description) {
        DockyEntry entry = new DockyEntry()
                .setHeader("Action Types")
                .setFactory(factory)
                .setDescription(description)
                .setType("entity_action_types");
        if(RA_Additions.getExamplePathRoot() != null) entry.setExamplePath(RA_Additions.getExamplePathRoot() + "\\testdata\\ra_additions\\actions\\entity\\" + factory.getSerializerId().getPath() + "_example.json");
        DockyRegistry.register(entry);
        Registry.register(ApoliRegistries.ENTITY_ACTION, factory.getSerializerId(), factory);
    }
}
