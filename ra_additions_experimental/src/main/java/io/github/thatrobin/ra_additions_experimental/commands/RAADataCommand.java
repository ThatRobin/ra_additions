package io.github.thatrobin.ra_additions_experimental.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.thatrobin.ra_additions_experimental.component.ClaimComponent;
import io.github.thatrobin.ra_additions_experimental.component.ClaimedLand;
import io.github.thatrobin.ra_additions_experimental.factories.mechanics.MechanicType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RAADataCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // TODO: Clean up this mess.
        dispatcher.register(
                literal("raadata").then(
                        literal("add").then(
                                argument("block", BlockPosArgumentType.blockPos())
                                        .then(argument("mechanic", MechanicTypeArgumentType.power()).executes(
                                        (command) -> {
                                            BlockPos pos = BlockPosArgumentType.getBlockPos(command, "block");
                                            MechanicType<?> mechanicType = MechanicTypeArgumentType.getMechanic(command, "mechanic");
                                            ClaimComponent component = ClaimComponent.CLAIM_DATA.get(command.getSource().getWorld());
                                            ClaimedLand land;
                                            if(component.getAllPos().contains(pos)) {
                                                land = component.getLand(pos);
                                            } else {
                                                land = new ClaimedLand();
                                            }
                                            land.addPower(mechanicType);
                                            component.addLand(pos,land);
                                            if(!command.getSource().getWorld().isClient) {
                                                component.syncWithAll();
                                            }
                                            return 1;
                                        }

                                )
                        ))).then(
                literal("get").then(
                        argument("block", BlockPosArgumentType.blockPos()).executes(
                                (command) -> {
                                    ServerCommandSource source = command.getSource();
                                    BlockPos pos = BlockPosArgumentType.getBlockPos(command, "block");
                                    ClaimComponent component = ClaimComponent.CLAIM_DATA.get(command.getSource().getWorld());
                                    if(component.getAllPos().contains(pos)) {
                                        NbtCompound compound = new NbtCompound();
                                        component.writeClaimedLandToNbt(compound, pos);
                                        Text data = Text.translatable(source.getWorld().getBlockState(pos).getBlock().getTranslationKey()).append(Text.literal(" contains data: ")).append(NbtHelper.toPrettyPrintedText(compound));
                                        source.sendFeedback(data, false);
                                        return 1;
                                    } else {
                                        source.sendError(Text.translatable(source.getWorld().getBlockState(pos).getBlock().getTranslationKey()).append(Text.literal(" has no Data")));
                                        return 0;
                                    }
                                }

                        )
                )

                )
        );
    }

}
