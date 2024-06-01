package io.github.tomasborsje;

import io.github.tomasborsje.commands.StartEventCommand;
import io.github.tomasborsje.core.GameRunner;
import io.github.tomasborsje.eventhandler.PlayerDeathHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DisasterSurvival extends JavaPlugin {
    public static Plugin plugin;
    /** Game runner that handles all the mini-game logic and ticking. **/
    public static GameRunner gameRunner = new GameRunner();
    @Override
    public void onEnable() {
        getLogger().info("DisasterSurvival enabling...");
        plugin = this;

        // Register commands and event listeners
        registerCommands();
        registerEvents();

        // Start tick runner
        gameRunner.runTaskTimer(this, 0, 1);

        getLogger().info("DisasterSurvival enabled.");
    }
    private void registerEvents() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerDeathHandler(), this);
    }

    private void registerCommands() {
        getCommand("startevent").setExecutor(new StartEventCommand());
    }

    @Override
    public void onDisable() {
        getLogger().info("DisasterSurvival disabled.");
    }
}
