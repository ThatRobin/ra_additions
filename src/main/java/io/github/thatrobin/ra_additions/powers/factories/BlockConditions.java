package io.github.thatrobin.ra_additions.powers.factories;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.data.RAA_DataTypes;
import io.github.thatrobin.ra_additions.util.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Collection;

public class BlockConditions {

    public static void register() {
        register(new ConditionFactory<>(RA_Additions.identifier("has_item_in_recipe"), new SerializableDataExt()
                .add("recipe_type", "The type of recipe to search for, possible values: `crafting`, `smelting`, `blasting`, `smoking`, `campfire_cooking`, `stonecutting` and `smithing`", RAA_DataTypes.RECIPE_TYPE, RecipeType.CRAFTING)
                .add("item_condition", "The condition used to check against the input items of the recipe", ApoliDataTypes.ITEM_CONDITION, null),
                (data, block) -> {
                    BlockState state = block.getBlockState();
                    ItemStack itemStack = state.getBlock().asItem().getDefaultStack();
                    ConditionFactory<ItemStack>.Instance condition = data.get("item_condition");
                    RecipeType<?> recipeType = data.get("recipe_type");
                    boolean found = false;
                    if(condition != null) {
                        try {
                            Recipe<?>[] recipes = RecipeFinder.getRecipesFor(itemStack, ((World)block.getWorld()).getRecipeManager(), condition, recipeType);
                            if(recipes.length > 0) {
                                found = true;
                            }
                        } catch (ClassCastException exception) {
                            RA_Additions.LOGGER.error(exception.getMessage());
                        }
                    } else {
                        found = true;
                    }
                    return found;
                }), "Checks if the recipe that outputs this block, has an item in it that matches this condition.");

        register(new ConditionFactory<>(RA_Additions.identifier("evaluate_condition"), new SerializableDataExt()
                .add("block_condition", "The Identifier of the tag or condition file to be evaluated", SerializableDataTypes.STRING),
                (data, block) -> {
                    String idStr = data.getString("block_condition");
                    if(idStr.startsWith("#")) {
                        Identifier id = Identifier.tryParse(idStr.substring(1));
                        Collection<ConditionType> conditions = BlockConditionTagManager.CONDITION_TAG_LOADER.getTag(id);
                        boolean result = true;
                        for (ConditionType condition : conditions) {
                            if(!condition.getCondition().test(block)) {
                                result = false;
                            }
                        }
                        return result;
                    } else {
                        Identifier id = Identifier.tryParse(idStr);
                        ConditionFactory<CachedBlockPosition>.Instance condition =  BlockConditionRegistry.get(id).getCondition();
                        return condition.test(block);
                    }
                }), "Evaluates a block condition that is stored in a file.");
    }

    @SuppressWarnings("SameParameterValue")
    private static void register(ConditionFactory<CachedBlockPosition> factory, String description) {
        DockyEntry entry = new DockyEntry()
                .setHeader("Condition Types")
                .setFactory(factory)
                .setDescription(description)
                .setType("block_condition_types");
        if(RA_Additions.getExamplePathRoot() != null) entry.setExamplePath(RA_Additions.getExamplePathRoot() + "\\testdata\\ra_additions\\conditions\\block\\" + factory.getSerializerId().getPath() + "_example.json");
        DockyRegistry.register(entry);
        Registry.register(ApoliRegistries.BLOCK_CONDITION, factory.getSerializerId(), factory);
    }
}
