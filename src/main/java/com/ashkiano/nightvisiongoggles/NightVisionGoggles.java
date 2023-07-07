package com.ashkiano.nightvisiongoggles;

import org.bukkit.plugin.java.JavaPlugin;

//TODO přidat metrics 18994
//TODO přidat permisi na příkaz
//TODO udělat nastavitelný název
//TODO udělat nastavitelné lore
//TODO přidat craftící recept
//TODO přidat vustom materiál na příkaz
//TODO hrac o efekt mozna neprijde když umře, ale tím si nejsem jistý, třeba otestovat
public class NightVisionGoggles extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getCommand("nightvisiongoggles").setExecutor(new GogglesCommand());

        getServer().getPluginManager().registerEvents(new GogglesListener(this), this);
    }

    @Override
    public void onDisable() {

    }
}