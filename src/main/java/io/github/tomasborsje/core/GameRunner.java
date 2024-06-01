package io.github.tomasborsje.core;

import io.github.tomasborsje.DisasterSurvival;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GameRunner extends BukkitRunnable {
    World overworld;
    GameState gameState = GameState.WAITING;
    List<DisasterEvent> runningEvents = new ArrayList<>();

    /**
     * Called every server tick.
     */
    @Override
    public void run() {
        // Check world exists before running
        if(overworld == null) {
            overworld = DisasterSurvival.plugin.getServer().getWorlds().getFirst();
            if(overworld == null) { return; }
            // World has loaded, init
            initialise();
        }

        // Do tick
        switch(gameState) {
            case WAITING: {
                break;
            }
            case RUNNING: {
                // If no events are running, set state to waiting
                if(runningEvents.isEmpty()) {
                    gameState = GameState.WAITING;
                    return;
                }
                // Tick all running events
                for (DisasterEvent event : runningEvents) {
                    event.tickEvent();
                    //Bukkit.getLogger().info("Ticked event " + event.getClass().getSimpleName());
                }
                // Get all inactive events
                List<DisasterEvent> inactiveEvents = runningEvents.stream().filter(event -> !event.isActive()).toList();

                // If any exist, remove them and print their names as a title
                if(!inactiveEvents.isEmpty()) {
                    // Get all event names and reduce them to a single comma-separated string
                    String eventNames = inactiveEvents.stream().map(DisasterEvent::getName).reduce((a, b) -> a + ", " + b).get();
                    inactiveEvents.forEach(event -> {
                        event.endEvent();
                        runningEvents.remove(event);
                        Bukkit.getLogger().info("Ended event " + event.getClass().getSimpleName());
                    });
                    // Broadcast title to all players
                    overworld.getPlayers().forEach(player -> player.sendTitle(ChatColor.GREEN + "You Survived!", eventNames, 10, 70, 20));
                }

                break;
            }
        }
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Called once, at the start of the server tick runner.
     */
    void initialise() {
        // Disable fire tick
        overworld.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        overworld.setGameRule(GameRule.DO_MOB_LOOT, false);
        overworld.setGameRule(GameRule.DO_TILE_DROPS, false);
        overworld.setGameRule(GameRule.DO_FIRE_TICK, false);
        overworld.setGameRule(GameRule.COMMAND_MODIFICATION_BLOCK_LIMIT, 1000000);
        overworld.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
        overworld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        overworld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        overworld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
    }

    /**
     * Add an event to the round and start it.
     * @param event The event to add.
     */
    public void addEvent(@Nonnull DisasterEvent event) {
        // If the round is running, add the event
        if(gameState == GameState.WAITING) {
            gameState = GameState.RUNNING;
            // Regenerate the arena at 0, 0, 0
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "clone -100 0 -100 -50 50 -50 0 -1 0");

            Bukkit.getLogger().info("Started event " + event.getClass().getSimpleName());
            runningEvents.add(event);
            event.startEvent();

            // Broadcast title to all players
            overworld.getPlayers().forEach(player -> player.sendTitle(event.getName(), event.getDescription(), 10, 70, 20));
        }
    }
}