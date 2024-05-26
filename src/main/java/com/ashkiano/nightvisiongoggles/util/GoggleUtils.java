package com.ashkiano.nightvisiongoggles.util;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;

// Borrowed from:
// https://github.com/BitByLogics/APIByLogic/blob/master/Spigot/src/main/java/net/bitbylogic/apibylogic/util/ItemStackUtil.java
public class GoggleUtils {

    /**
     * Create an ItemStack object from a configuration
     * section.
     *
     * @param section The configuration section.
     * @return New ItemStack instance.
     */
    public static ItemStack getItemStackFromConfig(ConfigurationSection section) {
        int amount = section.getInt("Amount", 1);
        ItemStack stack = new ItemStack(Material.valueOf(section.getString("Material", "BARRIER")), amount);
        ItemMeta meta = stack.getItemMeta();

        if (meta == null) {
            return null;
        }

        // Define the items name
        if (section.getString("Name") != null) {
            meta.setDisplayName(color(section.getString("Name")));
        }

        List<String> lore = Lists.newArrayList();

        // Define the items lore
        section.getStringList("Lore").forEach(string ->
                lore.add(color(string)));

        meta.setLore(lore);

        // Add flags to hide potion effects/attributes
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES);

        if (section.getBoolean("Unbreakable")) {
            meta.setUnbreakable(true);
        }

        // Make the item glow
        if (section.getBoolean("Glow")) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        // If leather armor, apply dye color if defined
        if (stack.getType().name().startsWith("LEATHER_") && section.getString("Dye-Color") != null) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) stack.getItemMeta();
            java.awt.Color color = ChatColor.of(section.getString("Dye-Color")).getColor();
            leatherArmorMeta.setColor(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
            stack.setItemMeta(leatherArmorMeta);
        }

        stack.setItemMeta(meta);

        // If the item is a potion, apply potion data
        if (stack.getType() == Material.SPLASH_POTION || stack.getType() == Material.POTION) {
            ConfigurationSection potionSection = section.getConfigurationSection("Potion-Data");

            if (potionSection != null) {
                PotionMeta potionMeta = (PotionMeta) meta;
                PotionEffectType type = PotionEffectType.getByName(potionSection.getString("Type", "POISON"));
                potionMeta.addCustomEffect(new PotionEffect(type, potionSection.getInt("Duration", 20), potionSection.getInt("Amplifier", 1) - 1), true);
                stack.setItemMeta(meta);
            }
        }

        if (stack.getType() == Material.TIPPED_ARROW) {
            PotionMeta potionMeta = (PotionMeta) stack.getItemMeta();
            potionMeta.setBasePotionData(new PotionData(PotionType.valueOf(section.getString("Arrow-Type", "POISON")), false, false));
            stack.setItemMeta(potionMeta);
        }

        // If the item is a player head, apply skin
        if (section.getString("Skull-Name") != null && stack.getType() == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) stack.getItemMeta();
            skullMeta.setOwner(section.getString("Skull-Name", "Notch"));
            stack.setItemMeta(skullMeta);
        }

        // Used for resourcepacks, to display custom models
        if (section.getInt("Model-Data") != 0) {
            ItemMeta updatedMeta = stack.getItemMeta();
            updatedMeta.setCustomModelData(section.getInt("Model-Data"));
            stack.setItemMeta(updatedMeta);
        }

        ItemMeta updatedMeta = stack.getItemMeta();

        // Apply enchantments
        section.getStringList("Enchantments").forEach(enchant -> {
            String[] data = enchant.split(":");
            NamespacedKey key = NamespacedKey.minecraft(data[0].trim());
            Enchantment enchantment = Enchantment.getByKey(key);
            int level = 0;

            try {
                level = Integer.parseInt(data[1]);
            } catch (NumberFormatException e) {
                Bukkit.getLogger().warning(String.format("[Night Vision Goggles]: Skipped enchantment '%s', invalid level.", enchant));
                return;
            }

            if (enchantment == null) {
                Bukkit.getLogger().warning(String.format("[Night Vision Goggles]: Skipped enchantment '%s', invalid enchant.", enchant));
                return;
            }

            updatedMeta.addEnchant(enchantment, level, true);
        });

        stack.setItemMeta(updatedMeta);

        return stack;
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
