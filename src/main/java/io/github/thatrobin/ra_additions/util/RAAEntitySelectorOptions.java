package io.github.thatrobin.ra_additions.util;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModComponents;
import io.github.thatrobin.ra_additions.mixins.EntitySelectorOptionsAccessor;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RAAEntitySelectorOptions {

    public static final DynamicCommandExceptionType INVALID_TYPE_EXCEPTION = new DynamicCommandExceptionType((entity) -> Text.translatable("argument.entity.options.type.invalid", entity));

    public static void register() {
        EntitySelectorOptionsAccessor.callPutOption("origin", (reader) -> {
            reader.setSuggestionProvider((builder, consumer) -> {
                CommandSource.suggestIdentifiers(OriginRegistry.identifiers(), builder, String.valueOf('!'));
                CommandSource.suggestIdentifiers(OriginRegistry.identifiers(), builder);
                return builder.buildFuture();
            });
            reader.setIncludesNonPlayers(false);
            int i = reader.getReader().getCursor();
            Identifier identifier = Identifier.fromCommandInput(reader.getReader());
            try {
                Origin origin = OriginRegistry.get(identifier);

                reader.setPredicate((entity) -> {
                    OriginComponent component = ModComponents.ORIGIN.get(entity);
                    return component.getOrigins().containsValue(origin);
                });
            } catch (Exception e) {
                reader.getReader().setCursor(i);
                throw INVALID_TYPE_EXCEPTION.createWithContext(reader.getReader(), identifier.toString());
            }
        }, (reader) -> true, Text.translatable("argument.entity.options.type.description"));
    }
}
