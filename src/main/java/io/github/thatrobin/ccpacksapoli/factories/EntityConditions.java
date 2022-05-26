package io.github.thatrobin.ccpacksapoli.factories;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ccpacksapoli.CCPacksApoli;
import io.github.thatrobin.ccpacksapoli.compat.TrinketsCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class EntityConditions {

    public static void register() {
        if(FabricLoader.getInstance().isModLoaded("trinkets")) {
            register(new ConditionFactory<>(CCPacksApoli.identifier("equipped_trinket"), new SerializableData()
                    .add("item_condition", ApoliDataTypes.ITEM_CONDITION),
                    (data, entity) -> {
                        if (entity instanceof PlayerEntity player) {
                            try {

                                return TrinketsCompat.trinketCheck(player, data.get("item_condition"));
                            } catch (Exception e) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }));
        }

        register(new ConditionFactory<>(CCPacksApoli.identifier("active_power_type"), new SerializableData()
                .add("power_type", SerializableDataTypes.IDENTIFIER)
                .add("blacklisted_powers", SerializableDataType.list(ApoliDataTypes.POWER_TYPE)),
                (data, entity) -> {
                    Identifier powerTypeId = data.getId("power_type");
                    List<PowerType<?>> blacklistedPowers = data.get("blacklisted_powers");
                    PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
                    List<PowerType<?>> correctPowers = Lists.newArrayList();

                    component.getPowerTypes(true).forEach(powerType -> {
                        if(!blacklistedPowers.contains(powerType) && component.hasPower(powerType)) {
                            Identifier allowedPowerId = ((powerType.getFactory()).getFactory().getSerializerId());
                            if (allowedPowerId.equals(powerTypeId)) {
                                correctPowers.add(powerType);
                            }
                        }
                    });

                    return correctPowers.stream().anyMatch(pt -> pt.isActive(entity));
                }));

        register(new ConditionFactory<>(CCPacksApoli.identifier("resource_percentage"), new SerializableData()
                .add("resource", ApoliDataTypes.POWER_TYPE)
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.EQUAL)
                .add("percentage", SerializableDataTypes.INT, 50),
                (data, entity) -> {
                    int resourceValue = 0;
                    float percentageValue = 0;
                    PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
                    Power p = component.getPower((PowerType<?>)data.get("resource"));
                    if(p instanceof VariableIntPower r) {
                        resourceValue = r.getValue();
                        percentageValue = ((float)resourceValue / (float)r.getMax()) * 100;

                    } else if(p instanceof CooldownPower cp) {
                        resourceValue = cp.getRemainingTicks();
                        percentageValue = ((float)resourceValue / (float)cp.cooldownDuration) * 100;
                    }
                    return ((Comparison)data.get("comparison")).compare(percentageValue, data.getInt("percentage"));
                }));
    }

    private static void register(ConditionFactory<Entity> conditionFactory) {
        Registry.register(ApoliRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
