package io.github.thatrobin.ra_additions_goals.factories.goals.utils;

import com.google.gson.JsonObject;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.Factory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;
import java.util.function.Function;

public class GoalFactory<G extends Goal> implements Factory {

    private final Identifier id;
    private boolean hasConditions = false;
    protected SerializableData data;
    protected Function<SerializableData.Instance, BiFunction<GoalType<?>, LivingEntity, G>>  factoryConstructor;

    public GoalFactory(Identifier id, SerializableData data, Function<SerializableData.Instance, BiFunction<GoalType<?>, LivingEntity, G>> factoryConstructor) {
        this.id = id;
        this.data = data;
        this.factoryConstructor = factoryConstructor;
    }

    @Override
    public Identifier getSerializerId() {
            return id;
        }

    public GoalFactory<G> allowCondition() {
        if(!hasConditions) {
            hasConditions = true;
            data.add("condition", ApoliDataTypes.ENTITY_CONDITION, null);
        }
        return this;
    }

    @Override
    public SerializableData getSerializableData() {
        return data;
    }

    public class Instance implements BiFunction<GoalType<?>, LivingEntity, G> {

        private final SerializableData.Instance dataInstance;

        private Instance(SerializableData.Instance data) {
                this.dataInstance = data;
            }

        @Override
        public G apply(GoalType<?> goalType, LivingEntity livingEntity) {
            BiFunction<GoalType<?>, LivingEntity, G> goalFactory = factoryConstructor.apply(dataInstance);
            G g = goalFactory.apply(goalType, livingEntity);
            if(hasConditions && dataInstance.isPresent("condition")) {
                g.addCondition(dataInstance.get("condition"));
            }
            return g;
        }
    }

    public Instance read(JsonObject json) {
            return new Instance(data.read(json));
        }

    @SuppressWarnings("unused")
    public Instance read(PacketByteBuf buffer) {
            return new Instance(data.read(buffer));
        }
}
