package io.github.thatrobin.ra_additions.powers;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.data.RAA_DataTypes;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

public class EntityBlockStoragePower extends Power {

    private ActionFactory<Pair<Entity, Triple<World, BlockPos, Direction>>>.Instance entityBlockAction;
    private ConditionFactory<Entity>.Instance entityCondition;
    private ConditionFactory<CachedBlockPosition>.Instance blockCondition;
    private Entity storedEntity;
    private CachedBlockPosition cachedBlockPos;
    private Triple<World, BlockPos, Direction> tripleBlock;

    public EntityBlockStoragePower(PowerType<?> type, LivingEntity entity, ActionFactory<Pair<Entity, Triple<World, BlockPos, Direction>>>.Instance entityBlockAction, ConditionFactory<Entity>.Instance entityCondition, ConditionFactory<CachedBlockPosition>.Instance blockCondition) {
        super(type, entity);
        this.storedEntity = entity;
        this.entityBlockAction = entityBlockAction;
        this.entityCondition = entityCondition;
        this.blockCondition = blockCondition;
    }

    public void setEntity(Entity storedEntity) {
        this.storedEntity = storedEntity;
    }

    public void setCachedBlockPos(Triple<World, BlockPos, Direction> tripleBlock) {
        this.cachedBlockPos = new CachedBlockPosition(tripleBlock.getLeft(), tripleBlock.getMiddle(), true);
        this.tripleBlock = tripleBlock;
    }

    public boolean doesApply() {
        return this.entityCondition.test(this.entity) && this.blockCondition.test(this.cachedBlockPos);
    }

    public void executeAction() {
        this.entityBlockAction.accept(new Pair<>(this.entity, this.tripleBlock));
    }


    @SuppressWarnings("rawtypes")
    public static PowerFactory createFactory() {
        return new PowerFactory<>(RA_Additions.identifier("item_use"),
                new SerializableDataExt()
                        .add("entityblock_action", "Sets a cooldown on the item (Similar to ender pearl cooldowns).", RAA_DataTypes.ENTITYBLOCK_ACTION, null)
                        .add("entity_condition", "The entity action to be executed on the player if specified.", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("block_condition", "If specified, only execute the action if the item condition is fulfilled.", ApoliDataTypes.BLOCK_CONDITION, null),
                data ->
                        (type, entity) -> new EntityBlockStoragePower(type, entity, data.get("entityblock_action"), data.get("entity_condition"), data.get("block_condition")))
                .allowCondition();
    }
}
