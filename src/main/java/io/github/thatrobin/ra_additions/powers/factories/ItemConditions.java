package io.github.thatrobin.ra_additions.powers.factories;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.data.RAA_DataTypes;
import io.github.thatrobin.ra_additions.util.RecipeFinder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registry;

public class ItemConditions {

    public static void register() {
        register(new ConditionFactory<>(RA_Additions.identifier("is_block"),
                new SerializableDataExt(),
                (data, stack) -> stack.getItem() instanceof BlockItem
                ), "Checks if the item extends BlockItem (Checks if the item is a block).");

        register(new ConditionFactory<>(RA_Additions.identifier("has_item_in_recipe"), new SerializableDataExt()
                .add("recipe_type", "The type of recipe to search for, possible values: `crafting`, `smelting`, `blasting`, `smoking`, `campfire_cooking`, `stonecutting` and `smithing`", RAA_DataTypes.RECIPE_TYPE, RecipeType.CRAFTING)
                .add("item_condition", "The condition used to check against the input items of the recipe", ApoliDataTypes.ITEM_CONDITION, null),
                (data, stack) -> {
                    ConditionFactory<ItemStack>.Instance condition = data.get("item_condition");
                    RecipeType<?> recipeType = data.get("recipe_type");
                    boolean found = false;
                    if(condition != null) {
                        try {
                            Recipe<?>[] recipes = RecipeFinder.getRecipesFor(stack, Apoli.server.getRegistryManager(), Apoli.server.getRecipeManager(), condition, recipeType);
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
                }), "Checks if the recipe used to create this item, has an item in it that matches this condition.");
    }

    @SuppressWarnings("SameParameterValue")
    private static void register(ConditionFactory<ItemStack> factory, String description) {
        DockyEntry entry = new DockyEntry()
                .setHeader("Condition Types")
                .setFactory(factory)
                .setDescription(description)
                .setType("item_condition_types");
        if(RA_Additions.getExamplePathRoot() != null) entry.setExamplePath(RA_Additions.getExamplePathRoot() + "\\testdata\\ra_additions\\conditions\\item\\" + factory.getSerializerId().getPath() + "_example.json");
        DockyRegistry.register(entry);
        Registry.register(ApoliRegistries.ITEM_CONDITION, factory.getSerializerId(), factory);
    }

}
