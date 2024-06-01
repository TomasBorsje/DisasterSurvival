package io.github.tomasborsje.eventhandler;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * On player death, intercepts the damage event and respawns the player at the spawn location in spectator mode.
 */
public class PlayerDeathHandler implements Listener {
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player player) {
            // If this damage event would kill the player, cancel it and set the player to spectator mode
            if(player.getHealth() - event.getFinalDamage() <= 0) {
                event.setCancelled(true);
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(new Location(player.getWorld(), 25, 25, 25));
            }
        }
    }
}
