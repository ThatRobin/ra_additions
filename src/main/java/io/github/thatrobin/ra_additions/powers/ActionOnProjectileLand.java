package io.github.thatrobin.ra_additions.powers;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnProjectileLand extends Power {

    private final Consumer<Pair<Entity, Entity>> bientityAction;
    private final Predicate<Pair<Entity,Entity>> bientityCondition;
    private final Predicate<CachedBlockPosition> blockCondition;
    private final Consumer<Triple<World, BlockPos, Direction>> blockAction;
    private final Identifier projectile;
    private final boolean shouldDamage;

    public ActionOnProjectileLand(PowerType<?> type, LivingEntity entity, Consumer<Triple<World, BlockPos, Direction>> blockAction, Predicate<CachedBlockPosition> blockCondition, Consumer<Pair<Entity, Entity>> bientityAction, Predicate<Pair<Entity,Entity>> bientityCondition, Identifier projectile, boolean shouldDamage) {
        super(type, entity);
        this.blockCondition = blockCondition;
        this.bientityAction = bientityAction;
        this.bientityCondition = bientityCondition;
        this.blockAction = blockAction;
        this.projectile = projectile;
        this.shouldDamage = shouldDamage;
    }

    public boolean shouldDamage() {
        return shouldDamage;
    }

    public boolean doesApplyEntity(Entity hit) {
        return isActive() && bientityCondition == null || bientityCondition.test(new Pair<>(this.entity, hit));
    }

    public boolean doesApplyBlock(BlockPos pos) {
        return blockCondition == null || blockCondition.test(new CachedBlockPosition(this.entity.world, pos, true));
    }

    public void executeEntityAction(Entity hit) {
        if (bientityAction != null) {
            bientityAction.accept(new Pair<>(this.entity, hit));
        }
    }

    public void executeBlockAction(BlockPos pos, Direction dir) {
        if (blockAction != null) {
            blockAction.accept(Triple.of(entity.world, pos, dir));
        }
    }

    public EntityType<?> getProjectile() {
        return Registries.ENTITY_TYPE.get(projectile);
    }

    public Identifier getProjectileId() {
        return projectile;
    }

    @SuppressWarnings("rawtypes")
    public static PowerFactory createFactory() {
        return new PowerFactory<>(RA_Additions.identifier("action_on_projectile_land"),
                new SerializableDataExt()
                        .add("projectile", "The identifier of the projectile entity.", SerializableDataTypes.IDENTIFIER, null)
                        .add("should_damage", "Determines if the entity will be damaged by the projectile.", SerializableDataTypes.BOOLEAN, false)
                        .add("bientity_action", "The bientity action to be executed between the entity hit, and the entity with this power.", ApoliDataTypes.BIENTITY_ACTION, null)
                        .add("bientity_condition", "If specified, only execute the bientity action if this condition is fulfilled.", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("block_action", "The block action to be executed if specified.", ApoliDataTypes.BLOCK_ACTION, null)
                        .add("block_condition", "If specified, only execute the specified actions if the block condition is fulfilled.", ApoliDataTypes.BLOCK_CONDITION, null),
                data ->
                        (type, entity) -> new ActionOnProjectileLand(type, entity, data.get("block_action"), data.get("block_condition"), data.get("bientity_action"), data.get("bientity_condition"), data.getId("projectile"), data.getBoolean("should_damage")))
                .allowCondition();
    }
}
