package io.github.thatrobin.ra_additions.mechanics;

import io.github.thatrobin.ra_additions.component.ClaimedLand;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

public class Mechanic  {

    protected MechanicType<?> type;
    protected ClaimedLand land;
    public NbtCompound element = new NbtCompound();

    public Mechanic(MechanicType<?> type, ClaimedLand land) {
        this.type = type;
        this.land = land;
    }

    public void fromTag(NbtElement tag) {
    }

    public NbtElement toTag() {
        return new NbtCompound();
    }

    public MechanicType<?> getType() {
        return type;
    }

    @SuppressWarnings("unused")
    public void executeBlockAction(Triple<World, BlockPos, Direction> data){
    }

    @SuppressWarnings("unused")
    public void executeEntityAction(Entity entity){
    }

    public void tick() {
    }

    public void setNbt(NbtCompound element) {
        this.element = element;
    }

    public NbtCompound getNbt() {
        return this.element;
    }

}