package io.github.thatrobin.ra_additions.powers.factories;

import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.PowerFactorySupplier;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.powers.*;
import net.minecraft.util.registry.Registry;

public class PowerFactories {

    public static void register() {
        register(new PowerFactory<>(RA_Additions.identifier("revenge"),
                new SerializableData(),
                data ->
                        (type, player) -> new RevengePower(type, player))
                .allowCondition());

        register(ActionOnProjectileLand::createFactory);
        register(AddGoalPower::createFactory);
        register(BindPower::createFactory);
        register(BorderPower::createFactory);
        register(BossBarPower::createFactory);
        register(BundlePower::createFactory);
        register(CustomTogglePower::createFactory);
        register(ItemUsePower::createFactory);
        register(RemoveGoalPower::createFactory);
        register(StatBarPower::createFactory);
    }

    private static void register(PowerFactory<?> serializer) {
        Registry.register(ApoliRegistries.POWER_FACTORY, serializer.getSerializerId(), serializer);
    }

    private static void register(PowerFactorySupplier<?> factorySupplier) {
        register(factorySupplier.createFactory());
    }
}
