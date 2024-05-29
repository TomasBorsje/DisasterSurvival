package io.github.tomasborsje.commands;

import io.github.tomasborsje.DisasterSurvival;
import io.github.tomasborsje.core.DisasterEvent;
import io.github.tomasborsje.core.GameState;
import io.github.tomasborsje.events.EarthquakeDisasterEvent;
import io.github.tomasborsje.events.FlashFireDisasterEvent;
import io.github.tomasborsje.events.FlashFloodDisasterEvent;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command handler for /startevent that starts a disaster event.
 */
public class StartEventCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // If no first arg, send error message
        if(args.length != 1) {
            sender.sendMessage(ChatColor.RED+"Please supply the event name!");
            return false;
        }

        // Match first argument to an event
        String eventName = args[0];
        World world = sender.getServer().getWorlds().getFirst();
        DisasterEvent event;
        switch(eventName.toLowerCase()) {
            case "flashfire" -> { event = new FlashFireDisasterEvent(world); break; }
            case "flood" -> { event = new FlashFloodDisasterEvent(world); break; }
            case "earthquake" -> { event = new EarthquakeDisasterEvent(world); break; }
            default -> { sender.sendMessage(ChatColor.RED+"Invalid event name!"); return false; }
        }

        // Start event on plugin's GameRunner
        DisasterSurvival.gameRunner.addEvent(event);
        DisasterSurvival.gameRunner.setGameState(GameState.RUNNING);
        sender.sendMessage(ChatColor.GREEN + "Stated "+event.getClass().getSimpleName() + " event!");
        return true;
    }
}
