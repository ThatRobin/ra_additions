package io.github.thatrobin.ra_additions_experimental.factories.mechanics;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_experimental.component.ClaimedLand;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

public class TickMechanic extends Mechanic {

    public ActionFactory<Triple<World, BlockPos, Direction>>.Instance action;
    public ConditionFactory<CachedBlockPosition>.Instance block_condition;
    private final int interval;
    private int totalTicks = 0;

    public TickMechanic(MechanicType<?> mechanicType, ClaimedLand land, int interval, ActionFactory<Triple<World, BlockPos, Direction>>.Instance action, ConditionFactory<CachedBlockPosition>.Instance block_condition) {
        super(mechanicType, land);
        this.action = action;
        this.interval = interval;
        this.block_condition = block_condition;
    }

    @Override
    public void tick(){
        if(totalTicks % this.interval == 0) {
            if (this.action != null) {
                if (this.block_condition != null) {
                    //if (this.block_condition.test(new CachedBlockPosition(land., data.getMiddle(), false))) {
                    //    this.action.accept(data);
                    //}
                    RA_Additions.LOGGER.info("tick");
                }
            }
        }
        totalTicks++;
    }

}
