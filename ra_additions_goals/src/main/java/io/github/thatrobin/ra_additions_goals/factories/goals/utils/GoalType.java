package io.github.thatrobin.ra_additions_goals.factories.goals.utils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GoalType<T extends Goal> {

    private final Identifier identifier;
    private final GoalFactory<T>.Instance factory;

    private String nameTranslationKey;
    private String descriptionTranslationKey;

    public GoalType(Identifier id, GoalFactory<T>.Instance factory) {
        this.identifier = id;
        this.factory = factory;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public GoalFactory<T>.Instance getFactory() {
        return factory;
    }

    public T create(LivingEntity entity) {
        return factory.apply(this, entity);
    }

    public String getOrCreateNameTranslationKey() {
        if(nameTranslationKey == null || nameTranslationKey.isEmpty()) {
            nameTranslationKey =
                    "item." + identifier.getNamespace() + "." + identifier.getPath() + ".name";
        }
        return nameTranslationKey;
    }

    @SuppressWarnings("unused")
    public Text getName() {
        return Text.translatable(getOrCreateNameTranslationKey());
    }

    public String getOrCreateDescriptionTranslationKey() {
        if(descriptionTranslationKey == null || descriptionTranslationKey.isEmpty()) {
            descriptionTranslationKey =
                    "item." + identifier.getNamespace() + "." + identifier.getPath() + ".description";
        }
        return descriptionTranslationKey;
    }

    @SuppressWarnings("unused")
    public Text getDescription() {
        return Text.translatable(getOrCreateDescriptionTranslationKey());
    }

    @Override
    public int hashCode() {
        return this.identifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof GoalType)) {
            return false;
        }
        Identifier id = ((GoalType<?>)obj).getIdentifier();
        return identifier.equals(id);
    }
}

