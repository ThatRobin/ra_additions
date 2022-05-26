package io.github.thatrobin.ccpacksapoli.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import io.github.thatrobin.ccpacksapoli.CCPacksApoli;

public class ModComponents implements EntityComponentInitializer {

    public static final ComponentKey<ChoiceComponent> CHOICE;

    static {
        CHOICE = ComponentRegistry.getOrCreate(CCPacksApoli.identifier("choice"), ChoiceComponent.class);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(CHOICE, PlayerChoiceComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }

}
