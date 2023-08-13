package io.github.thatrobin.ra_additions_experimental.factories.mechanics;

import com.google.gson.JsonObject;
import io.github.apace100.calio.data.SerializableData;
import io.github.thatrobin.ra_additions_experimental.component.ClaimedLand;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;
import java.util.function.Function;
public class MechanicFactory<P extends Mechanic> {

    private final Identifier id;
    protected SerializableData data;
    protected Function<SerializableData.Instance, BiFunction<MechanicType<? extends Mechanic>, ClaimedLand, P>>  factoryConstructor;

    public MechanicFactory(Identifier id, SerializableData data, Function<SerializableData.Instance, BiFunction<MechanicType<? extends Mechanic>, ClaimedLand, P>> factoryConstructor) {
        this.id = id;
        this.data = data;
        this.factoryConstructor = factoryConstructor;
    }

    public Identifier getSerializerId() {
        return id;
    }

    public class Instance implements BiFunction<MechanicType<? extends Mechanic>, ClaimedLand, P> {

        private final SerializableData.Instance dataInstance;
        public Item item;

        private Instance(SerializableData.Instance data) {
            this.dataInstance = data;
        }

        @SuppressWarnings("unused")
        public void write(PacketByteBuf buf) {
            buf.writeIdentifier(id);
            data.write(buf, dataInstance);
        }

        @Override
        public P apply(MechanicType<? extends Mechanic> mechanicType, ClaimedLand claimedLand) {
            BiFunction<MechanicType<? extends Mechanic>, ClaimedLand, P> mechanicFactory = factoryConstructor.apply(dataInstance);
            return mechanicFactory.apply(mechanicType, claimedLand);
        }
    }

    public Instance read(JsonObject json) {
        return new Instance(data.read(json));
    }

    public Instance read(PacketByteBuf buffer) {
        return new Instance(data.read(buffer));
    }
}