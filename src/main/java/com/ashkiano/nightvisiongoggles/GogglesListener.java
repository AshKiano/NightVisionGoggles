package com.ashkiano.nightvisiongoggles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GogglesListener implements Listener {

    private final JavaPlugin plugin;
    private String permission_use;

    public GogglesListener(JavaPlugin plugin, String permission) {
        this.plugin = plugin;
        this.permission_use = permission;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> checkForGoggles(player), 1L);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> checkForGoggles(player), 1L);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> checkForGoggles(player), 1L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        checkForGoggles(player);
    }

    private void checkForGoggles(Player player) {

        if (!player.hasPermission(permission_use)) {
            return;
        }

        ItemStack helmet = player.getInventory().getHelmet();

        if (helmet != null && helmet.getType() == Material.LEATHER_HELMET) {
            ItemMeta meta = helmet.getItemMeta();

            if (meta == null) return;
            if (meta.hasLore() && meta.getLore().contains(GogglesCommand.GOGGLES_LORE)) {
                if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false));
                }
                return;
            }
        }

        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }
}