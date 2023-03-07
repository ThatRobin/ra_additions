package io.github.thatrobin.ra_additions.util;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import net.minecraft.util.Identifier;

public class ConditionType {

    private final ConditionFactory<?>.Instance factory;
    private final Identifier identifier;

    public ConditionType(Identifier id, ConditionFactory<?>.Instance factory) {
        this.identifier = id;
        this.factory = factory;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public <T> ConditionFactory<T>.Instance getCondition() {
        return (ConditionFactory<T>.Instance) factory;
    }
}
