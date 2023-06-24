package io.github.thatrobin.ra_additions_tags.data_loaders;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import net.minecraft.util.Identifier;

@SuppressWarnings("unchecked")
public class ActionType {

    private final ActionFactory<?>.Instance factory;
    private final Identifier identifier;

    public ActionType(Identifier id, ActionFactory<?>.Instance factory) {
        this.identifier = id;
        this.factory = factory;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public <T> ActionFactory<T>.Instance getAction() {
        return (ActionFactory<T>.Instance) factory;
    }
}
