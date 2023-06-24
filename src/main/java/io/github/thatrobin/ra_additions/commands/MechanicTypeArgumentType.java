package io.github.thatrobin.ra_additions.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.thatrobin.ra_additions.mechanics.MechanicRegistry;
import io.github.thatrobin.ra_additions.mechanics.MechanicType;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class MechanicTypeArgumentType implements ArgumentType<Identifier> {

    public static MechanicTypeArgumentType power() {
        return new MechanicTypeArgumentType();
    }

    public Identifier parse(StringReader reader) throws CommandSyntaxException {
        return Identifier.fromCommandInput(reader);
    }

    public static MechanicType<?> getMechanic(CommandContext<ServerCommandSource> context, String argumentName) {
        return MechanicRegistry.get(context.getArgument(argumentName, Identifier.class));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(MechanicRegistry.identifiers(), builder);
    }
}
