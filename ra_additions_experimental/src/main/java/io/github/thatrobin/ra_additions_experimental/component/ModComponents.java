package io.github.thatrobin.ra_additions_experimental.component;

import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;

public class ModComponents implements WorldComponentInitializer {

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(ClaimComponent.CLAIM_DATA,ClaimComponentImpl::new);
    }

}
