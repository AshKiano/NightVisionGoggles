package com.ashkiano.nightvisiongoggles;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

//TODO přidat permisi na používání brýlí
//TODO udělat nastavitelný název
//TODO udělat nastavitelné lore
//TODO přidat craftící recept
//TODO přidat custom materiál na příkaz
public class NightVisionGoggles extends JavaPlugin {

    private String permission;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        permission = config.getString("command_permission", "nightvisiongoggles");
        this.getCommand("nightvisiongoggles").setExecutor(new GogglesCommand(permission));
        getServer().getPluginManager().registerEvents(new GogglesListener(this), this);

        Metrics metrics = new Metrics(this, 18994);
    }

    @Override
    public void onDisable() {

    }
}