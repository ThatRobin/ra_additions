package io.github.thatrobin.ra_additions_tags.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import io.github.thatrobin.ra_additions_tags.data_loaders.ActionType;
import io.github.thatrobin.ra_additions_tags.data_loaders.EntityActionTagManager;
import io.github.thatrobin.ra_additions_tags.registries.BiEntityActionRegistry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;

public class BiEntityActionTypeArgumentType implements ArgumentType<BiEntityActionTypeArgumentType.ActionArgument> {

    private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_TAG_EXCEPTION = new DynamicCommandExceptionType((id) -> Text.translatable("arguments.action.tag.unknown", id));
    private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_EXCEPTION = new DynamicCommandExceptionType((id) -> Text.translatable("arguments.action.unknown", id));

    public static BiEntityActionTypeArgumentType action() {
        return new BiEntityActionTypeArgumentType();
    }
    
    public ActionArgument parse(StringReader stringReader) throws CommandSyntaxException {
        final Identifier identifier;
        if (stringReader.canRead() && stringReader.peek() == '#') {
            stringReader.skip();
            identifier = Identifier.fromCommandInput(stringReader);
            return context -> BiEntityActionTypeArgumentType.getActionTag(identifier);
        } else {
            identifier = Identifier.fromCommandInput(stringReader);
            return context -> Collections.singleton(BiEntityActionTypeArgumentType.getAction(identifier));
        }
    }

    static ActionType getAction(Identifier id) throws CommandSyntaxException {
        ActionType type =  BiEntityActionRegistry.get(id);
        if(type == null) throw UNKNOWN_FUNCTION_EXCEPTION.create(id.toString());
        return type;
    }

    static Collection<ActionType> getActionTag(Identifier id) throws CommandSyntaxException {
        Collection<ActionType> tag = EntityActionTagManager.ACTION_TAG_LOADER.getTagOrEmpty(id);
        if(tag == null) throw UNKNOWN_FUNCTION_TAG_EXCEPTION.create(id.toString());
        return tag;
    }

    public static Collection<ActionType> getActions(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return (context.getArgument(name, ActionArgument.class)).getActions(context);
    }

    public interface ActionArgument {
        Collection<ActionType> getActions(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;
    }

}
