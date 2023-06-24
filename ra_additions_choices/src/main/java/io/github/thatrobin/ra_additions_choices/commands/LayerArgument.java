package io.github.thatrobin.ra_additions_choices.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.thatrobin.ra_additions_choices.choice.ChoiceLayer;
import io.github.thatrobin.ra_additions_choices.choice.ChoiceLayers;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class LayerArgument implements ArgumentType<Identifier> {

    public static final DynamicCommandExceptionType LAYER_NOT_FOUND = new DynamicCommandExceptionType((object) -> Text.translatable("commands.choice.layer_not_found", object));

    public static LayerArgument layer() {
        return new LayerArgument();
    }

    public Identifier parse(StringReader p_parse_1_) throws CommandSyntaxException {
        return Identifier.fromCommandInput(p_parse_1_);
    }

    public ChoiceLayer getLayer(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
        Identifier id = context.getArgument(argumentName, Identifier.class);

        try {
            return ChoiceLayers.getLayer(id);
        } catch (IllegalArgumentException var4) {
            throw LAYER_NOT_FOUND.create(id);
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(ChoiceLayers.getLayers().stream().map(ChoiceLayer::getIdentifier), builder);
    }
}
