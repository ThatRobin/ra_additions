package io.github.thatrobin.ra_additions_tags.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_tags.data_loaders.*;
import io.github.thatrobin.ra_additions_tags.registries.BiEntityActionRegistry;
import io.github.thatrobin.ra_additions_tags.registries.BlockActionRegistry;
import io.github.thatrobin.ra_additions_tags.registries.EntityActionRegistry;
import io.github.thatrobin.ra_additions_tags.registries.ItemActionRegistry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RAAActionCommand {

    public static final SuggestionProvider<ServerCommandSource> ENTITY_ACTIONS = (context, builder) -> {
        CommandSource.suggestIdentifiers(EntityActionTagManager.ACTION_TAG_LOADER.getTags(), builder, "#");
        return CommandSource.suggestIdentifiers(EntityActionRegistry.identifiers(), builder);
    };

    public static final SuggestionProvider<ServerCommandSource> BLOCK_ACTIONS = (context, builder) -> {
        CommandSource.suggestIdentifiers(BlockActionTagManager.ACTION_TAG_LOADER.getTags(), builder, "#");
        return CommandSource.suggestIdentifiers(BlockActionRegistry.identifiers(), builder);
    };

    public static final SuggestionProvider<ServerCommandSource> ITEM_ACTIONS = (context, builder) -> {
        CommandSource.suggestIdentifiers(ItemActionTagManager.ACTION_TAG_LOADER.getTags(), builder, "#");
        return CommandSource.suggestIdentifiers(ItemActionRegistry.identifiers(), builder);
    };

    public static final SuggestionProvider<ServerCommandSource> BIENTITY_ACTIONS = (context, builder) -> {
        CommandSource.suggestIdentifiers(BiEntityActionTagManager.ACTION_TAG_LOADER.getTags(), builder, "#");
        return CommandSource.suggestIdentifiers(BiEntityActionRegistry.identifiers(), builder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("raa").then(
                        literal("action").then(
                                literal("entity").requires(cs -> cs.hasPermissionLevel(2))
                                        .then(
                                                argument("targets", EntityArgumentType.entities()).then(
                                                        argument("action", EntityActionTypeArgumentType.action()).suggests(ENTITY_ACTIONS).executes(
                                                                (command) -> {
                                                                        int i = 0;
                                                                        try {
                                                                            Collection<? extends Entity> targets = EntityArgumentType.getEntities(command, "targets");
                                                                            Collection<ActionType> actions = EntityActionTypeArgumentType.getActions(command, "action");
                                                                            for(Entity target : targets) {
                                                                                if(target instanceof LivingEntity) {
                                                                                    for (ActionType actionType : actions) {
                                                                                        actionType.getAction().accept(target);
                                                                                        i++;
                                                                                    }
                                                                                }
                                                                            }
                                                                        } catch (Exception e) {
                                                                            command.getSource().sendError(Text.literal(e.getMessage()));
                                                                        }
                                                                        return i;
                                                                }
                                                        )
                                                )
                                        )
                        ).then(
                                literal("block").then(
                                        argument("pos", BlockPosArgumentType.blockPos()).then(
                                                argument("action", BlockActionTypeArgumentType.action()).suggests(BLOCK_ACTIONS).executes(
                                                        (command) -> {
                                                            int i = 0;
                                                            try {
                                                                BlockPos blockPos = BlockPosArgumentType.getBlockPos(command, "pos");
                                                                Collection<ActionType> actions = BlockActionTypeArgumentType.getActions(command, "action");
                                                                for (ActionType action : actions) {
                                                                    Triple<World, BlockPos, Direction> actionParams = Triple.of(command.getSource().getWorld(), blockPos, Direction.UP);
                                                                    action.getAction().accept(actionParams);
                                                                    i++;
                                                                }

                                                            } catch (Exception e) {
                                                                command.getSource().sendError(Text.literal(e.getMessage()));
                                                            }
                                                            return i;
                                                        }
                                                        )
                                        )
                                )
                        ).then(
                                literal("item").then(
                                        argument("targets", EntityArgumentType.entities()).then(
                                                argument("sourceSlot", ItemSlotArgumentType.itemSlot()).then(
                                                        argument("action", ItemActionTypeArgumentType.action()).suggests(ITEM_ACTIONS).executes(
                                                                (command) -> {
                                                                    int i = 0;
                                                                    try {
                                                                        Collection<? extends Entity> targets = EntityArgumentType.getEntities(command, "targets");
                                                                        int slot = ItemSlotArgumentType.getItemSlot(command, "sourceSlot");
                                                                        Collection<ActionType> actions = ItemActionTypeArgumentType.getActions(command, "action");
                                                                        for(Entity target : targets) {
                                                                            if(target instanceof LivingEntity) {
                                                                                StackReference stack = target.getStackReference(slot);
                                                                                for (ActionType action : actions) {
                                                                                    Pair<World, ItemStack> actionParams = new Pair<>(target.getWorld(), stack.get());
                                                                                    action.getAction().accept(actionParams);
                                                                                    i++;
                                                                                }
                                                                            }
                                                                        }


                                                                    } catch (Exception e) {
                                                                        command.getSource().sendError(Text.literal(e.getMessage()));
                                                                    }
                                                                    return i;
                                                                }
                                                        )
                                                )
                                        )
                                )
                        ).then(
                                literal("bientity").then(
                                        argument("actor", EntityArgumentType.entity()).then(
                                                argument("targets", EntityArgumentType.entities()).then(
                                                        argument("action", BiEntityActionTypeArgumentType.action()).suggests(BIENTITY_ACTIONS).executes(
                                                                (command) -> {
                                                                    int i = 0;
                                                                    try {
                                                                        Entity actor = EntityArgumentType.getEntity(command, "actor");
                                                                        Collection<? extends Entity> targets = EntityArgumentType.getEntities(command, "targets");
                                                                        Collection<ActionType> actions = BiEntityActionTypeArgumentType.getActions(command, "action");
                                                                        for(Entity target : targets) {
                                                                            if(target instanceof LivingEntity) {
                                                                                for (ActionType action : actions) {
                                                                                    if(!actor.equals(target)) {
                                                                                        Pair<Entity, Entity> actionParams = new Pair<>(actor, target);
                                                                                        action.getAction().accept(actionParams);
                                                                                        i++;
                                                                                    } else {
                                                                                        RA_Additions.LOGGER.warn("The actor entity is the same as the target entity.");
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    } catch (Exception e) {
                                                                        command.getSource().sendError(Text.literal(e.getMessage()));
                                                                    }
                                                                    return i;
                                                                }
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

}
