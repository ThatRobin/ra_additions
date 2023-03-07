package io.github.thatrobin.ra_additions.powers.factories;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.docky.utils.SectionTitleManager;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.compat.TrinketsCompat;
import io.github.thatrobin.ra_additions.util.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.apache.commons.compress.utils.Lists;

import java.util.Collection;
import java.util.List;

public class EntityConditions {

    public static void register(String label) {
        SectionTitleManager.put("Condition Types", "entity_condition");

        register(new ConditionFactory<>(RA_Additions.identifier("active_power_type"), new SerializableDataExt(label)
                .add("power_type", "The namespace ID of the power type which will be checked to see if any are active.", SerializableDataTypes.IDENTIFIER)
                .add("blacklisted_powers", "The namespace IDs of powers that will be excluded from the check.", SerializableDataType.list(ApoliDataTypes.POWER_TYPE)),
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

        register(new ConditionFactory<>(RA_Additions.identifier("resource_percentage"), new SerializableDataExt(label)
                .add("resource", "The Identifier of the power type that defines the resource which exists on the player.", ApoliDataTypes.POWER_TYPE)
                .add("comparison", "How the value of the power that will be evaluated should be compared to the specified value.", ApoliDataTypes.COMPARISON, Comparison.EQUAL)
                .add("percentage", "The percentage value to compare the value of the power that will be evaluated to. `(e.g 50%)`", SerializableDataTypes.INT, 50),
                (data, entity) -> {
                    int resourceValue;
                    float percentageValue = 0;
                    PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
                    Power p = component.getPower((PowerType<?>)data.get("resource"));
                    if(p instanceof VariableIntPower r) {
                        resourceValue = r.getValue();
                        percentageValue = ((float)resourceValue / ((float)r.getMax()) - (float)r.getMin()) * 100;

                    } else if(p instanceof CooldownPower cp) {
                        resourceValue = cp.getRemainingTicks();
                        percentageValue = ((float)resourceValue / (float)cp.cooldownDuration) * 100;
                    }
                    return ((Comparison)data.get("comparison")).compare(percentageValue, data.getInt("percentage"));
                }));

        register(new ConditionFactory<>(RA_Additions.identifier("evaluate_condition"), new SerializableDataExt(label)
                .add("entity_condition", "The Identifier of the tag or condition file to be evaluated", SerializableDataTypes.STRING),
                (data, entity) -> {
                    String idStr = data.getString("entity_condition");
                    if(idStr.startsWith("#")) {
                        Identifier id = Identifier.tryParse(idStr.substring(1));
                        Collection<ConditionType> conditions = EntityConditionTagManager.CONDITION_TAG_LOADER.getTag(id);
                        boolean result = true;
                        for (ConditionType condition : conditions) {
                            if(!condition.getCondition().test(entity)) {
                                result = false;
                            }
                        }
                        return result;
                    } else {
                        Identifier id = Identifier.tryParse(idStr);
                        ConditionFactory<Entity>.Instance condition =  EntityConditionRegistry.get(id).getCondition();
                        return condition.test(entity);
                    }
                }));

        if(FabricLoader.getInstance().isModLoaded("trinkets")) {
            register(new ConditionFactory<>(RA_Additions.identifier("equipped_trinket"), new SerializableDataExt(label)
                    .add("item_condition", "The items that are searched for in the trinket slots.", ApoliDataTypes.ITEM_CONDITION),
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

    }

    private static void register(ConditionFactory<Entity> factory) {
        Registry.register(ApoliRegistries.ENTITY_CONDITION, factory.getSerializerId(), factory);
    }
}
