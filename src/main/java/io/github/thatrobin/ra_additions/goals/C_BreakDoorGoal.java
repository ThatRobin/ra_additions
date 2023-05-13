package io.github.thatrobin.ra_additions.goals;

import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.Goal;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;
import io.github.thatrobin.ra_additions.goals.factories.GoalType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

public class C_BreakDoorGoal extends Goal {

    public C_BreakDoorGoal(GoalType<?> goalType, LivingEntity livingEntity, int priority) {
        super(goalType, livingEntity, Type.GOAL);
        this.setPriority(priority);
        this.setGoal(new BreakDoorGoal((HostileEntity) livingEntity, (difficulty) -> difficulty == Difficulty.HARD) {
            @Override
            public boolean canStart() {
                if (!super.canStart()) {
                    return false;
                } else if (!this.mob.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                    return false;
                } else {
                    return this.isDifficultySufficient(this.mob.world.getDifficulty()) && !this.isDoorOpen() && doesApply(this.mob);
                }
            }
        });
    }

    @SuppressWarnings("rawtypes")
    public static GoalFactory createFactory() {
        return new GoalFactory<>(RA_Additions.identifier("break_door"), new SerializableDataExt()
                .add("priority", SerializableDataTypes.INT, 0),
                data ->
                        (type, entity) -> new C_BreakDoorGoal(type, entity, data.getInt("priority")))
                .allowCondition();
    }

}
