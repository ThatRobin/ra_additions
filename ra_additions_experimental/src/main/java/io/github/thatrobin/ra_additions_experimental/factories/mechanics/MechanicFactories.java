package io.github.thatrobin.ra_additions_experimental.factories.mechanics;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions_experimental.registries.RAA_Registries;
import net.minecraft.registry.Registry;

public class MechanicFactories {

    public static void register() {
        register(new MechanicFactory<>(RA_Additions.identifier("tick"),
                new SerializableData()
                        .add("interval", SerializableDataTypes.INT, 1)
                        .add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
                        .add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null),
                data ->
                        (identifier, claimedLand) -> new TickMechanic(identifier, claimedLand, data.getInt("interval"), data.get("block_action"), data.get("block_condition")))
        );

        register(new MechanicFactory<>(RA_Additions.identifier("resource"),
                new SerializableData()
                        .add("min", SerializableDataTypes.INT, 0)
                        .add("max", SerializableDataTypes.INT, 1)
                        .add("start_value", SerializableDataTypes.INT, 0),
                data ->
                        (identifier, claimedLand) -> new ResourceMechanic(identifier, claimedLand, data.getInt("start_value"), data.getInt("min"), data.getInt("max")))
        );

        register(new MechanicFactory<>(RA_Additions.identifier("on_use"),
                new SerializableData()
                        .add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null),
                data ->
                        (identifier, claimedLand) -> new UseMechanic(identifier, claimedLand, data.get("entity_action"), data.get("block_action"), data.get("block_condition")))
        );

        register(new MechanicFactory<>(RA_Additions.identifier("on_step"),
                new SerializableData()
                        .add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null),
                data ->
                        (identifier, claimedLand) -> new StepMechanic(identifier, claimedLand, data.get("entity_action"), data.get("block_action"), data.get("block_condition")))
        );

        register(new MechanicFactory<>(RA_Additions.identifier("on_neighbour_update"),
                new SerializableData()
                        .add("self_action", ApoliDataTypes.BLOCK_ACTION, null)
                        .add("neighbour_action", ApoliDataTypes.BLOCK_ACTION, null)
                        .add("self_condition", ApoliDataTypes.BLOCK_CONDITION, null)
                        .add("neighbour_condition", ApoliDataTypes.BLOCK_CONDITION, null),
                data ->
                        (identifier, claimedLand) -> new NeighourUpdateMechanic(identifier, claimedLand, data.get("self_action"), data.get("neighbour_action"), data.get("self_condition"), data.get("neighbour_condition")))
        );

        register(new MechanicFactory<>(RA_Additions.identifier("on_land"),
                new SerializableData()
                        .add("damage_multiplier", SerializableDataTypes.FLOAT, 1.0f)
                        .add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
                        .add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null),
                data ->
                        (identifier, claimedLand) -> new FallMechanic(identifier, claimedLand, data.getFloat("damage_multiplier"), data.get("entity_action"), data.get("block_action"), data.get("block_condition")))
        );

        register(new MechanicFactory<>(RA_Additions.identifier("triggerable"),
                new SerializableData()
                        .add("self_action", ApoliDataTypes.BLOCK_ACTION, null)
                        .add("self_condition", ApoliDataTypes.BLOCK_CONDITION, null)
                        .add("neighbour_action", ApoliDataTypes.BLOCK_ACTION, null)
                        .add("neighbour_condition", ApoliDataTypes.BLOCK_CONDITION, null),
                data ->
                        (identifier, claimedLand) -> new FindBlockMechanic(identifier, claimedLand, data.get("self_action"), data.get("self_condition"), data.get("neighbour_action"), data.get("neighbour_condition")))
        );

        register(new MechanicFactory<>(RA_Additions.identifier("modify_block_render"),
                new SerializableData(),
                data ->
                        (identifier, claimedLand) -> new ModifyBlockRenderMechanic(identifier, claimedLand))
        );
    }

    private static void register(MechanicFactory<? extends Mechanic> serializer) {
        Registry.register(RAA_Registries.MECHANIC_FACTORY, serializer.getSerializerId(), serializer);
    }

}
