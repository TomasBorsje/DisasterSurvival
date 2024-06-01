package io.github.tomasborsje.core;

import io.github.tomasborsje.DisasterSurvival;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Class that represents a disaster event that can occur during a round.
 * Derived classes should implement all event logic required for an event.
 */
public abstract class DisasterEvent {
    public static final int DISASTER_DURATION = 20*60;
    public static final int ARENA_MIN_X = 0;
    public static final int ARENA_MAX_X = 50;
    public static final int ARENA_MIN_Z = 0;
    public static final int ARENA_MAX_Z = 50;
    public static final int ARENA_MIN_Y = 0;
    public static final int ARENA_MAX_Y = 50;
    private final String name;
    private final String description;
    protected World world;
    protected int ticksRemaining;
    private boolean isActive;

    public DisasterEvent(World world, String name, String description) {
        // Set up the event
        this.world = world;
        this.name = name;
        this.description = description;
    }

    /**
     * Called when the event starts.
     */
    public void startEvent() {
        // Start the event
        isActive = true;
        ticksRemaining = DISASTER_DURATION;
        respawnDeadPlayers();
    }

    /**
     * Put any spectator players at 25, 5, 25 and in adventure mode again
     */
    protected void respawnDeadPlayers() {
        for(Player player : world.getPlayers()) {
            if(player.getGameMode() == GameMode.SPECTATOR) {
                player.teleport(new Location(world, 25, 5, 25));
                player.setGameMode(GameMode.ADVENTURE);
            }
        }
    }

    /**
     * Called every tick while the event is active.
     */
    public void tickEvent() {
        // Called every tick
        ticksRemaining--;
        if(ticksRemaining <= 0) {
            endEvent();
        }
    }

    /**
     * Called when the event ends.
     */
    public void endEvent() {
        // End the event
        isActive = false;
        // To any living players, play the challenge complete sound
        for(Player player : world.getPlayers()) {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 100, 1.5f);
        }
        respawnDeadPlayers();
    }

    /**
     * Returns whether the event is actively running.
     * @return True if the event is running, false otherwise.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Returns the name of the event.
     * @return The name of the event.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the event.
     * @return The description of the event.
     */
    public String getDescription() {
        return description;
    }
}
