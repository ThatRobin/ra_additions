package io.github.thatrobin.ccpacksapoli.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.thatrobin.ccpacksapoli.CCPacksApoli;
import io.github.thatrobin.ccpacksapoli.choice.Choice;
import io.github.thatrobin.ccpacksapoli.choice.ChoiceLayer;
import io.github.thatrobin.ccpacksapoli.component.ChoiceComponent;
import io.github.thatrobin.ccpacksapoli.component.ModComponents;
import io.github.thatrobin.ccpacksapoli.networking.CCPacksModPackets;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ChoiceCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("choice").requires(cs -> cs.hasPermissionLevel(2))
                        .then(literal("gui").then(argument("targets", EntityArgumentType.entities()).then(argument("layer", LayerArgument.layer()).executes((command) -> {
                                try {
                                    ChoiceLayer l = LayerArgument.layer().getLayer(command, "layer");
                                    Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(command, "targets");

                                    targets.forEach(target -> {
                                        ChoiceComponent component = ModComponents.CHOICE.get(target);
                                        if (l.isEnabled()) {
                                            component.setChoice(l, Choice.EMPTY);
                                        }

                                        component.checkAutoChoosingLayers(target);
                                        component.sync();
                                        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
                                        data.writeBoolean(false);
                                        data.writeIdentifier(l.getIdentifier());
                                        ServerPlayNetworking.send(target, CCPacksModPackets.OPEN_CHOICE_SCREEN, data);
                                    });
                                    command.getSource().sendFeedback(new TranslatableText("commands.choice.gui.layer", targets.size(), new TranslatableText(l.getTranslationKey())), false);
                                    return targets.size();
                                } catch (Exception e) {
                                    CCPacksApoli.LOGGER.info(e.getMessage());
                                }
                            return 0;
                        })))));
    }


}
