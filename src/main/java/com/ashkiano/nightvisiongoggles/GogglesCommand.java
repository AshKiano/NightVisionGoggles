package com.ashkiano.nightvisiongoggles;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GogglesCommand implements CommandExecutor {

    public static final String GOGGLES_NAME = "Night Vision Goggles";
    public static final String GOGGLES_LORE = ChatColor.GRAY + "Night vision goggles";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            ItemStack goggles = new ItemStack(Material.LEATHER_HELMET);
            ItemMeta meta = goggles.getItemMeta();

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