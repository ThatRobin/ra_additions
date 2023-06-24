package io.github.thatrobin.ra_additions.mechanics;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.thatrobin.ra_additions.component.ClaimedLand;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

public class DDNeighourUpdateMechanic extends Mechanic {

    public ActionFactory<Triple<World, BlockPos, Direction>>.Instance block_action;
    public ActionFactory<Triple<World, BlockPos, Direction>>.Instance neighbour_action;
    public ConditionFactory<CachedBlockPosition>.Instance block_condition;
    public ConditionFactory<CachedBlockPosition>.Instance neighbour_condition;

    public DDNeighourUpdateMechanic(MechanicType<?> mechanicType, ClaimedLand claimedLand, ActionFactory<Triple<World, BlockPos, Direction>>.Instance block_action, ActionFactory<Triple<World, BlockPos, Direction>>.Instance neighbour_action, ConditionFactory<CachedBlockPosition>.Instance block_condition, ConditionFactory<CachedBlockPosition>.Instance neighbour_condition) {
        super(mechanicType, claimedLand);
        this.block_action = block_action;
        this.neighbour_action = neighbour_action;
        this.block_condition = block_condition;
        this.neighbour_condition = neighbour_condition;
    }

    @Override
    public void executeBlockAction(Triple<World, BlockPos, Direction> data) {
        boolean passed = true;
        if (this.block_condition != null) {
            CachedBlockPosition position = new CachedBlockPosition(data.getLeft(), data.getMiddle(), false);
            if(!this.block_condition.test(position)) {
                passed = false;
            }
        }
        if(passed) {
            if (this.block_action != null) {
                this.block_action.accept(data);
            }
        }
    }

    @SuppressWarnings("unused")
    public void executeNeighborAction(Triple<World, BlockPos, Direction> data){
        boolean passed = true;
        if (this.neighbour_condition != null) {
            CachedBlockPosition position = new CachedBlockPosition(data.getLeft(), data.getMiddle(), false);
            if(!this.neighbour_condition.test(position)) {
                passed = false;
            }
        }
        if(passed) {
            if (this.neighbour_action != null) {
                this.neighbour_action.accept(data);
            }
        }
    }

}
