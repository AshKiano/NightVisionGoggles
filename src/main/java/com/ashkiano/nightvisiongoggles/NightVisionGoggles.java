package com.ashkiano.nightvisiongoggles;

import com.ashkiano.nightvisiongoggles.util.GoggleUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

//TODO přidat nastavení, že půjde vypnout craftící recept jednou proměnnou v configu
//TODO givuje brýle dvakrát na příkaz a po sundání brýlí se neodebere nekonečný nightvision efekt
public class NightVisionGoggles extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getCommand("nightvisiongoggles").setExecutor(new GogglesCommand(this));
        getServer().getPluginManager().registerEvents(new GogglesListener(this), this);

        new Metrics(this, 18994);

        getLogger().info("Thank you for using the NightVisionGoggles plugin! If you enjoy using this plugin," +
                " please consider making a donation to support the development. You can donate at: https://donate.ashkiano.com");

        checkForUpdates();
        loadRecipe();
    }

    public void loadRecipe() {
        NamespacedKey recipeKey = new NamespacedKey(this, "goggles");

        if (Bukkit.getRecipe(recipeKey) != null) {
            Bukkit.removeRecipe(recipeKey);
        }

        ItemStack goggleItem = GoggleUtils.getItemStackFromConfig(getConfig().getConfigurationSection("Goggles-Item"));
        ItemMeta meta = goggleItem.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(this, "goggles"), PersistentDataType.BOOLEAN, true);
        goggleItem.setItemMeta(meta);

        ConfigurationSection recipeSection = getConfig().getConfigurationSection("Goggle-Recipe");

        if (recipeSection == null) {
            return;
        }

        String recipeType = recipeSection.getString("Recipe-Type", "SHAPELESS");

        switch (recipeType) {
            case "SHAPED":
                List<String> shape = recipeSection.getStringList("Shape");
                ShapedRecipe shapedRecipe = new ShapedRecipe(recipeKey, goggleItem).shape(shape.toArray(new String[]{}));

                for (String ingredientKey : recipeSection.getConfigurationSection("Ingredients").getKeys(false)) {
                    ItemStack ingredient = GoggleUtils.getItemStackFromConfig(recipeSection.getConfigurationSection("Ingredients." + ingredientKey));
                    shapedRecipe.setIngredient(ingredientKey.charAt(0), new RecipeChoice.ExactChoice(ingredient));
                }

                Bukkit.addRecipe(shapedRecipe);
                break;
            case "SHAPELESS":
                ShapelessRecipe shapelessRecipe = new ShapelessRecipe(recipeKey, goggleItem);

                for (String ingredientKey : recipeSection.getConfigurationSection("Ingredients").getKeys(false)) {
                    ItemStack ingredient = GoggleUtils.getItemStackFromConfig(recipeSection.getConfigurationSection("Ingredients." + ingredientKey));
                    shapelessRecipe.addIngredient(new RecipeChoice.ExactChoice(ingredient));
                }
                Bukkit.addRecipe(shapelessRecipe);
                break;
            default:
                break;
        }
    }

    private void checkForUpdates() {
        try {
            String pluginName = this.getDescription().getName();
            URL url = new URL("https://www.ashkiano.com/version_check.php?plugin=" + pluginName);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                JSONObject jsonObject = new JSONObject(jsonResponse);
                if (jsonObject.has("error")) {
                    this.getLogger().warning("Error when checking for updates: " + jsonObject.getString("error"));
                } else {
                    String latestVersion = jsonObject.getString("latest_version");

                    String currentVersion = this.getDescription().getVersion();
                    if (currentVersion.equals(latestVersion)) {
                        this.getLogger().info("This plugin is up to date!");
                    } else {
                        this.getLogger().warning("There is a newer version (" + latestVersion + ") available! Please update!");
                    }
                }
            } else {
                this.getLogger().warning("Failed to check for updates. Response code: " + responseCode);
            }
        } catch (Exception e) {
            this.getLogger().warning("Failed to check for updates. Error: " + e.getMessage());
        }
    }
}