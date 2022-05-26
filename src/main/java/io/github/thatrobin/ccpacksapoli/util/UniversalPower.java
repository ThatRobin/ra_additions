package io.github.thatrobin.ccpacksapoli.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import io.github.apace100.apoli.power.MultiplePowerType;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ccpacksapoli.CCPacksApoli;
import io.github.thatrobin.ccpacksapoli.data.CCPacksApoliDataTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class UniversalPower {

    private final Identifier identifier;
    public List<PowerType<?>> powerTypes = new LinkedList<>();
    public List<EntityType<?>> entities = Lists.newArrayList();

    public static final SerializableData DATA = new SerializableData()
            .add("powers", SerializableDataTypes.IDENTIFIERS, Lists.newArrayList())
            .add("entity_entry", CCPacksApoliDataTypes.ENTITY_ENTRY, null);

    public UniversalPower(Identifier id) {
        this.identifier = id;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public static UniversalPower fromJson(Identifier id, JsonObject json) {
        return createFromData(id, DATA.read(json));
    }

    @SuppressWarnings("unchecked")
    public static UniversalPower createFromData(Identifier id, SerializableData.Instance data) {
        UniversalPower universalPower = new UniversalPower(id);

        ((List<Identifier>)data.get("powers")).forEach(powerId -> {
            try {
                PowerType<?> powerType = PowerTypeRegistry.get(powerId);
                if(powerType instanceof MultiplePowerType) {
                    ImmutableList<Identifier> subPowers = ((MultiplePowerType<?>)powerType).getSubPowers();
                    for(Identifier subPowerId : subPowers) {
                        universalPower.add(PowerTypeRegistry.get(subPowerId));
                    }
                } else {
                    universalPower.add(powerType);
                }
            } catch(IllegalArgumentException e) {
                CCPacksApoli.LOGGER.error("Powerset \"" + id + "\" contained unregistered power: \"" + powerId + "\"");
            }
        });
        if(data.isPresent("entity_entry")) {
            universalPower.entities = (List<EntityType<?>>)data.get("entity_entry");
        }

        return universalPower;
    }

    public UniversalPower add(PowerType<?>... powerTypes) {
        this.powerTypes.addAll(Lists.newArrayList(powerTypes));
        return this;
    }

}
