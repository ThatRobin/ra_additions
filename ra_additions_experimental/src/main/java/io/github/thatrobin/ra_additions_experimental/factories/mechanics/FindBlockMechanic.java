package io.github.thatrobin.ra_additions_experimental.factories.mechanics;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.thatrobin.ra_additions_experimental.component.ClaimedLand;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

public class FindBlockMechanic extends Mechanic {

    public ActionFactory<Triple<World, BlockPos, Direction>>.Instance self_action;
    public ActionFactory<Triple<World, BlockPos, Direction>>.Instance neighbour_action;
    public ConditionFactory<CachedBlockPosition>.Instance self_condition;
    public ConditionFactory<CachedBlockPosition>.Instance neighbour_condition;

    public FindBlockMechanic(MechanicType<?> mechanicType, ClaimedLand claimedLand, ActionFactory<Triple<World, BlockPos, Direction>>.Instance self_action, ConditionFactory<CachedBlockPosition>.Instance self_condition, ActionFactory<Triple<World, BlockPos, Direction>>.Instance neighbour_action, ConditionFactory<CachedBlockPosition>.Instance neighbour_condition) {
        super(mechanicType, claimedLand);
        this.self_action = self_action;
        this.neighbour_action = neighbour_action;
        this.self_condition = self_condition;
        this.neighbour_condition = neighbour_condition;
    }

    @SuppressWarnings("unused")
    public void executeAction(Triple<World, BlockPos, Direction> data) {
        World world = data.getLeft();
        BlockPos blockPos = data.getMiddle();
        Direction[] directions = Direction.values();
        for (Direction direction : directions) {
            BlockPos blockPos2 = blockPos.offset(direction);
            BlockEntity blockEntity = world.getBlockEntity(blockPos2);
            if (blockEntity != null) {
                Triple<World, BlockPos, Direction> data2 = Triple.of(world, blockPos2, direction);
                CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(world, blockPos, false);
                CachedBlockPosition cachedBlockPosition2 = new CachedBlockPosition(world, blockPos2, false);
                if(this.self_action != null) {
                    if (this.self_condition != null) {
                        if (this.self_condition.test(cachedBlockPosition)) {
                            this.self_action.accept(data);
                        }
                    } else {
                        this.self_action.accept(data);
                    }
                }
                if(this.neighbour_action != null) {
                    if (this.neighbour_condition != null) {
                        if (this.neighbour_condition.test(cachedBlockPosition2)) {
                            this.neighbour_action.accept(data2);
                        }
                    } else {
                        this.neighbour_action.accept(data2);
                    }
                }
            }
        }

    }

}
