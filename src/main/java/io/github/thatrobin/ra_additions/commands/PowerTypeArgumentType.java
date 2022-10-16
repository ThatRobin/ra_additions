package io.github.thatrobin.ra_additions.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.thatrobin.ra_additions.util.PowerTagManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;

public class PowerTypeArgumentType implements ArgumentType<PowerTypeArgumentType.PowerArgument> {

    private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_TAG_EXCEPTION = new DynamicCommandExceptionType((id) -> Text.translatable("arguments.power.tag.unknown", id));
    private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_EXCEPTION = new DynamicCommandExceptionType((id) -> Text.translatable("arguments.power.unknown", id));

    public static PowerTypeArgumentType power() {
        return new PowerTypeArgumentType();
    }
    
    public PowerArgument parse(StringReader stringReader) throws CommandSyntaxException {
        final Identifier identifier;
        if (stringReader.canRead() && stringReader.peek() == '#') {
            stringReader.skip();
            identifier = Identifier.fromCommandInput(stringReader);
            return new PowerArgument() {
                public Collection<PowerType<?>> getPowers(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
                    return PowerTypeArgumentType.getPowerTag(context, identifier);
                }

                public Pair<Identifier, Either<PowerType<?>, Collection<PowerType<?>>>> getPowerOrTag(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
                    return Pair.of(identifier, Either.right(PowerTypeArgumentType.getPowerTag(context, identifier)));
                }
            };
        } else {
            identifier = Identifier.fromCommandInput(stringReader);
            return new PowerArgument() {
                public Collection<PowerType<?>> getPowers(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
                    return Collections.singleton(PowerTypeArgumentType.getPower(context, identifier));
                }

                public Pair<Identifier, Either<PowerType<?>, Collection<PowerType<?>>>> getPowerOrTag(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
                    return Pair.of(identifier, Either.left(PowerTypeArgumentType.getPower(context, identifier)));
                }
            };
        }
    }

    static PowerType<?> getPower(CommandContext<ServerCommandSource> context, Identifier id) throws CommandSyntaxException {
        PowerType<?> type =  PowerTypeRegistry.get(id);
        if(type == null) throw UNKNOWN_FUNCTION_EXCEPTION.create(id.toString());
        return type;
    }

    static Collection<PowerType<?>> getPowerTag(CommandContext<ServerCommandSource> context, Identifier id) throws CommandSyntaxException {
        Collection<PowerType<?>> tag = PowerTagManager.POWER_TAG_LOADER.getTagOrEmpty(id);
        if(tag == null) throw UNKNOWN_FUNCTION_TAG_EXCEPTION.create(id.toString());
        return tag;
    }

    public static Collection<PowerType<?>> getPowers(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return (context.getArgument(name, PowerArgument.class)).getPowers(context);
    }

    public static Pair<Identifier, Either<PowerType<?>, Collection<PowerType<?>>>> getPowerOrTag(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return (context.getArgument(name, PowerArgument.class)).getPowerOrTag(context);
    }

    public interface PowerArgument {
        Collection<PowerType<?>> getPowers(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;

        Pair<Identifier, Either<PowerType<?>, Collection<PowerType<?>>>> getPowerOrTag(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;
    }

}
