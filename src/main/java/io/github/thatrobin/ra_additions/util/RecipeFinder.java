package io.github.thatrobin.ra_additions.util;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;

import java.util.ArrayList;
import java.util.List;

public class RecipeFinder {

    public static Recipe<?>[] getRecipesFor(ItemStack inputStack, DynamicRegistryManager registryManager, RecipeManager recipeManager, ConditionFactory<ItemStack>.Instance condition, RecipeType<?> recipeType) {
        List<Recipe<?>> recipes = new ArrayList<>();

        if (!inputStack.isEmpty()) {
            for (Recipe<?> recipe : recipeManager.values()) {
                if (recipe.getType().equals(recipeType) && matches(inputStack, recipe.getOutput(registryManager))) {
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        for (ItemStack matchingStack : ingredient.getMatchingStacks()) {
                            if(condition == null || condition.test(matchingStack)) {
                                if(!recipes.contains(recipe)) {
                                    recipes.add(recipe);
                                }
                            }
                        }

                    }

                }
            }
        }
        return recipes.toArray(new Recipe<?>[0]);
    }

    private static boolean matches(ItemStack input, ItemStack output) {
        return input.getItem() == output.getItem() && input.getCount() >= output.getCount();
    }
}
