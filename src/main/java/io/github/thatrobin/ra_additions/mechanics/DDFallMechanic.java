package io.github.thatrobin.ra_additions.mechanics;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.thatrobin.ra_additions.component.ClaimedLand;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

public class DDFallMechanic extends Mechanic {

    public ActionFactory<Triple<World, BlockPos, Direction>>.Instance block_action;
    public ActionFactory<Entity>.Instance entity_action;
    public float damage_multipler;
    public ConditionFactory<CachedBlockPosition>.Instance block_condition;

    public DDFallMechanic(MechanicType<?> mechanicType, ClaimedLand claimedLand, float damage_multipler, ActionFactory<Entity>.Instance entity_action, ActionFactory<Triple<World, BlockPos, Direction>>.Instance block_action, ConditionFactory<CachedBlockPosition>.Instance block_condition) {
        super(mechanicType, claimedLand);
        this.damage_multipler = damage_multipler;
        this.block_action = block_action;
        this.entity_action = entity_action;
        this.block_condition = block_condition;
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

    @Override
    public void executeEntityAction(Entity entity) {
        if (this.entity_action == null)
            return;
        this.entity_action.accept(entity);
    }

}
