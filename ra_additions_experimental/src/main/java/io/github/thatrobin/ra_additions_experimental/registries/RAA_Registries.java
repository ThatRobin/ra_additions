package io.github.thatrobin.ra_additions_experimental.registries;

import io.github.apace100.calio.ClassUtil;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_experimental.factories.mechanics.Mechanic;
import io.github.thatrobin.ra_additions_experimental.factories.mechanics.MechanicFactory;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.SimpleRegistry;

@SuppressWarnings("deprecation")
public class RAA_Registries {
    public static final SimpleRegistry<MechanicFactory<? extends Mechanic>> MECHANIC_FACTORY = FabricRegistryBuilder.createSimple(ClassUtil.<MechanicFactory<? extends Mechanic>>castClass(MechanicFactory.class), RA_Additions.identifier("mechanic_factory")).buildAndRegister();
}
