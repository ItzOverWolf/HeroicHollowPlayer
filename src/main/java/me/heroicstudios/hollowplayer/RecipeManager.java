package me.heroicstudios.hollowplayer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;
import java.util.Map;

public class RecipeManager {

    public static void registerRecipes(HollowPlayer plugin) {
        ConfigurationSection recipesSection = plugin.getConfig().getConfigurationSection("recipes");
        if (recipesSection == null) return;

        for (String key : recipesSection.getKeys(false)) {
            ConfigurationSection section = recipesSection.getConfigurationSection(key);
            if (section == null || !section.getBoolean("enabled", true)) continue;

            Ability ability = Ability.fromId(key);
            if (ability == null) continue;

            NamespacedKey recipeKey = new NamespacedKey(plugin, "recipe_" + key);
            
            // Remove old recipe if it exists (for reloads)
            Bukkit.removeRecipe(recipeKey);

            List<String> shape = section.getStringList("shape");
            if (shape.size() != 3) {
                plugin.getLogger().warning("Invalid recipe shape for " + key + "! Must have 3 rows.");
                continue;
            }

            ShapedRecipe recipe = new ShapedRecipe(recipeKey, ability.getItem(plugin));
            recipe.shape(shape.get(0), shape.get(1), shape.get(2));

            ConfigurationSection ingredients = section.getConfigurationSection("ingredients");
            if (ingredients != null) {
                for (String charKey : ingredients.getKeys(false)) {
                    String matName = ingredients.getString(charKey);
                    Material material = Material.matchMaterial(matName != null ? matName : "");
                    if (material != null) {
                        recipe.setIngredient(charKey.charAt(0), material);
                    } else {
                        plugin.getLogger().warning("Invalid material '" + matName + "' for recipe " + key);
                    }
                }
            }

            Bukkit.addRecipe(recipe);
        }
    }
}
