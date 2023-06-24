package io.github.thatrobin.ra_additions.powers;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class NbtPower extends Power {

    private NbtCompound compound;

    public NbtPower(PowerType<?> type, LivingEntity entity, NbtCompound compound) {
        super(type, entity);
        this.compound = compound;
    }

    public void setCompound(NbtCompound compound) {
        this.compound = compound;
    }

    public NbtCompound getCompound() {
        return compound;
    }

    @Override
    public NbtElement toTag() {
        return compound;
    }

    @Override
    public void fromTag(NbtElement tag) {
        compound = (NbtCompound) tag;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(RA_Additions.identifier("nbt"),
                new SerializableDataExt()
                        .add("start_value", SerializableDataTypes.NBT, new NbtCompound()),
                data ->
                        (type, player) -> {
                            return new NbtPower(type, player, data.get("start_value"));
                        })

                .allowCondition();
    }
}
