package io.github.thatrobin.ra_additions.mixins;

import net.minecraft.command.EntitySelectorOptions;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Predicate;

@Mixin(EntitySelectorOptions.class)
public interface EntitySelectorOptionsAccessor {
    @Invoker
    static void callPutOption(String ignoredId, EntitySelectorOptions.SelectorHandler ignoredHandler, Predicate<EntitySelectorReader> ignoredCondition, Text ignoredDescription) {
        throw new NoSuchMethodError();
    }
}