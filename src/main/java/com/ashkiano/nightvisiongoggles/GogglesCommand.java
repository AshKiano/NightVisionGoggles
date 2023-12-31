package com.ashkiano.nightvisiongoggles;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class GogglesCommand implements CommandExecutor {

    public static String GOGGLES_NAME;
    public static String GOGGLES_LORE;

    private String permission;
    private JavaPlugin plugin;

    public GogglesCommand(String permission, JavaPlugin plugin) {
        this.permission = permission;
        this.plugin = plugin;
        GOGGLES_NAME = this.plugin.getConfig().getString("nightvisiongoggles-name", "Night Vision Goggles");
        GOGGLES_LORE = ChatColor.GRAY + this.plugin.getConfig().getString("nightvisiongoggles-lore", "Night vision goggles");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission(permission)) {
                //TODO udělat hlášku konfigurovatelnou
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return true;
            }

            ItemStack goggles = new ItemStack(Material.LEATHER_HELMET);
            ItemMeta meta = goggles.getItemMeta();

            if (meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) meta;
                leatherArmorMeta.setColor(Color.LIME);
                meta = leatherArmorMeta;
            }

            if (meta == null) return false;
            meta.setDisplayName(GOGGLES_NAME);
            meta.setLore(Arrays.asList(GOGGLES_LORE));
            goggles.setItemMeta(meta);

            player.getInventory().addItem(goggles);

            return true;
        }

        return false;
    }
}