package io.github.thatrobin.ra_additions.goals;

import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.Goal;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;
import io.github.thatrobin.ra_additions.goals.factories.GoalType;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.LongDoorInteractGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;

public class C_DoorInteractGoal extends Goal {

    public C_DoorInteractGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.setGoal(new LongDoorInteractGoal((MobEntity) livingEntity, false) {
            @Override
            public boolean canStart() {
                if (!NavigationConditions.hasMobNavigation(this.mob)) {
                    return false;
                }
                if (!this.mob.horizontalCollision) {
                    return false;
                }
                MobNavigation mobNavigation = (MobNavigation)this.mob.getNavigation();
                mobNavigation.setCanPathThroughDoors(true);
                Path path = mobNavigation.getCurrentPath();
                if (path == null || path.isFinished()) {
                    return false;
                }
                for (int i = 0; i < Math.min(path.getCurrentNodeIndex() + 2, path.getLength()); ++i) {
                    PathNode pathNode = path.getNode(i);
                    this.doorPos = new BlockPos(pathNode.x, pathNode.y + 1, pathNode.z);
                    if (this.mob.squaredDistanceTo(this.doorPos.getX(), this.mob.getY(), this.doorPos.getZ()) > 2.25) continue;
                    this.doorValid = DoorBlock.isWoodenDoor(this.mob.world, this.doorPos);
                    if (!this.doorValid) continue;
                    return doesApply(this.mob);
                }
                this.doorPos = this.mob.getBlockPos().up();
                this.doorValid = DoorBlock.isWoodenDoor(this.mob.world, this.doorPos);
                return this.doorValid && doesApply(this.mob);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("door_interact"), new SerializableDataExt()
                .add("priority", SerializableDataTypes.INT, 0),
                data ->
                        (type, entity) -> new C_DoorInteractGoal(type, entity, data.getInt("priority")))
                .allowCondition();
    }

}
