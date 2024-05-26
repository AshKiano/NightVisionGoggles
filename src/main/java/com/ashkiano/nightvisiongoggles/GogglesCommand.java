package com.ashkiano.nightvisiongoggles;

import com.ashkiano.nightvisiongoggles.util.GoggleUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;

public class GogglesCommand implements CommandExecutor {

    private NightVisionGoggles plugin;

    public GogglesCommand(NightVisionGoggles plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sender.sendMessage(GoggleUtils.color(plugin.getConfig().getString("Messages.Console-Command.Usage")));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(GoggleUtils.color(plugin.getConfig().getString("Messages.Console-Command.Invalid-Player")));
                return true;
            }

            ItemStack goggleItem = GoggleUtils.getItemStackFromConfig(plugin.getConfig().getConfigurationSection("Goggles-Item"));
            ItemMeta meta = goggleItem.getItemMeta();
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "goggles"), PersistentDataType.BOOLEAN, true);
            goggleItem.setItemMeta(meta);

            HashMap<Integer, ItemStack> leftOverItems = target.getInventory().addItem(goggleItem);
            leftOverItems.values().forEach(item -> target.getWorld().dropItemNaturally(target.getLocation(), item));
            sender.sendMessage(GoggleUtils.color(plugin.getConfig().getString("Messages.Console-Command.Success").replace("%player%", target.getName())));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            if (!player.hasPermission(plugin.getConfig().getString("Permissions.Command"))) {
                sender.sendMessage(GoggleUtils.color(plugin.getConfig().getString("Messages.Player-Command.No-Permission")));
                return true;
            }

            ItemStack goggleItem = GoggleUtils.getItemStackFromConfig(plugin.getConfig().getConfigurationSection("Goggles-Item"));
            ItemMeta meta = goggleItem.getItemMeta();
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "goggles"), PersistentDataType.BOOLEAN, true);
            goggleItem.setItemMeta(meta);
            HashMap<Integer, ItemStack> leftOverItems = player.getInventory().addItem(goggleItem);
            leftOverItems.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));

            player.getInventory().addItem(goggleItem);
            sender.sendMessage(GoggleUtils.color(plugin.getConfig().getString("Messages.Player-Command.Success")));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission(plugin.getConfig().getString("Permissions.Reload"))) {
                sender.sendMessage(GoggleUtils.color(plugin.getConfig().getString("Messages.Player-Command.No-Permission")));
                return true;
            }

            plugin.reloadConfig();
            plugin.loadRecipe();
            sender.sendMessage(GoggleUtils.color(plugin.getConfig().getString("Messages.Reload.Success")));
            return true;
        }

        if (!player.hasPermission(plugin.getConfig().getString("Permissions.Give-Other"))) {
            sender.sendMessage(GoggleUtils.color(plugin.getConfig().getString("Messages.Give-Other.No-Permission")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(GoggleUtils.color(plugin.getConfig().getString("Messages.Give-Other.Invalid-Player")));
            return true;
        }

        ItemStack goggleItem = GoggleUtils.getItemStackFromConfig(plugin.getConfig().getConfigurationSection("Goggles-Item"));
        ItemMeta meta = goggleItem.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "goggles"), PersistentDataType.BOOLEAN, true);
        goggleItem.setItemMeta(meta);

        HashMap<Integer, ItemStack> leftOverItems = target.getInventory().addItem(goggleItem);
        leftOverItems.values().forEach(item -> target.getWorld().dropItemNaturally(target.getLocation(), item));
        sender.sendMessage(GoggleUtils.color(plugin.getConfig().getString("Messages.Give-Other.Success").replace("%player%", target.getName())));
        return true;
    }
}