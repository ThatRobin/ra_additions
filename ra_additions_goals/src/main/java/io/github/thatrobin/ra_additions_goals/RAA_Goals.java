package io.github.thatrobin.ra_additions_goals;

import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalFactories;
import io.github.thatrobin.ra_additions_goals.factories.goals.utils.GoalTypes;
import io.github.thatrobin.ra_additions_goals.factories.powers.utils.PowerFactories;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class RAA_Goals implements ModInitializer {
    @Override
    public void onInitialize() {
        GoalFactories.register();
        PowerFactories.register();

        registerResourceListeners();
    }

    public void registerResourceListeners() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new GoalTypes());
    }
}
