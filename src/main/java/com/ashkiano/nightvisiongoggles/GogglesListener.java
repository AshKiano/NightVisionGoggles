package com.ashkiano.nightvisiongoggles;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GogglesListener implements Listener {

    private final NightVisionGoggles plugin;

    public GogglesListener(NightVisionGoggles plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> checkForGoggles((Player) event.getWhoClicked()), 1L);
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
        if (!player.hasPermission(plugin.getConfig().getString("Permissions.Use"))) {
            return;
        }

        ItemStack helmet = player.getInventory().getHelmet();

        if (helmet == null) {
            return;
        }

        ItemMeta meta = helmet.getItemMeta();

        if (meta == null || !meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "goggles"), PersistentDataType.BOOLEAN)) {
            if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION) || !player.getPotionEffect(PotionEffectType.NIGHT_VISION).isInfinite()) {
                return;
            }

            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, -1, 0, false, false, false));
    }
}