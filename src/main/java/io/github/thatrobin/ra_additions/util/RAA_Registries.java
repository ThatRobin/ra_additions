package io.github.thatrobin.ra_additions.util;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.ClassUtil;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

@SuppressWarnings("rawtypes")
public class RAA_Registries {
    public static final Registry<GoalFactory> TASK_FACTORY;
    public static final Registry<ActionFactory<Pair<Entity, Triple<World, BlockPos, Direction>>>> ENTITYBLOCK_ACTION;

    static {
        TASK_FACTORY = FabricRegistryBuilder.createSimple(GoalFactory.class, RA_Additions.identifier("task_factory")).buildAndRegister();
        ENTITYBLOCK_ACTION = FabricRegistryBuilder.createSimple(ClassUtil.<ActionFactory<Pair<Entity, Triple<World, BlockPos, Direction>>>>castClass(ConditionFactory.class), RA_Additions.identifier("entityblock_action")).buildAndRegister();
    }
}
