package io.github.thatrobin.ra_additions_choices.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import io.github.thatrobin.ra_additions.RA_Additions;

public class ModComponents implements EntityComponentInitializer {

    public static final ComponentKey<ChoiceComponent> CHOICE = ComponentRegistry.getOrCreate(RA_Additions.identifier("choice"), ChoiceComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(CHOICE, PlayerChoiceComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }

}
