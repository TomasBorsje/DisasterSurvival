package io.github.tomasborsje.disasters;

import io.github.tomasborsje.DisasterSurvival;
import io.github.tomasborsje.core.DisasterEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class FlashFireDisasterEvent extends DisasterEvent {

    public static final int INITIAL_FIRE_COUNT = 6;
    public static final double FIRE_SPREAD_PROBABILITY = 0.5;

    public FlashFireDisasterEvent(World world) {
        super(world, "Flash Fire", "Avoid the spreading flames!");
    }

    public void startEvent() {
        super.startEvent();

        // Sleep for 1 second
        Bukkit.getScheduler().runTaskLater(DisasterSurvival.plugin, () -> {
            Bukkit.getLogger().info("Flash fire event started!");
            // Start the event
            for(int i = 0; i < INITIAL_FIRE_COUNT; i++) {
                int x = (int) (Math.random() * 50);
                int z = (int) (Math.random() * 50);
                int y = ARENA_MIN_Y;
                // Go up from y=0 until we find an air block
                while(!world.getBlockAt(x, y, z).getType().isAir() && y < ARENA_MAX_Y) {
                    y++;
                }
                Bukkit.getLogger().info("Starting fire at "+x+", "+y+", "+z);
                // If air, place fire
                if(world.getBlockAt(x, y, z).getType().isAir()) {
                    world.getBlockAt(x, y, z).setType(org.bukkit.Material.FIRE);
                }
            }
        }, 40L);
    }

    @Override
    public void tickEvent() {
        super.tickEvent();
        // Every 5 ticks, spread all existing fires out in a random direction by 1 block
        if(ticksRemaining % 5 == 0) {
            for(int x = ARENA_MIN_X; x < ARENA_MAX_X; x++) {
                for(int y = ARENA_MIN_Y; y < ARENA_MAX_Y; y++) {
                    for (int z = ARENA_MIN_Z; z < ARENA_MAX_Z; z++) {
                        if (world.getBlockAt(x, y, z).getType() == org.bukkit.Material.FIRE) {
                            // Random chance of 50% to spread
                            if (Math.random() > FIRE_SPREAD_PROBABILITY) {
                                continue;
                            }
                            // Random choice between -1, 0, 1 for dx, dy, dz
                            int dx = (int) (Math.random() * 3) - 1;
                            int dy = (int) (Math.random() * 3) - 1;
                            int dz = (int) (Math.random() * 3) - 1;
                            // If air, place fire
                            if (world.getBlockAt(x + dx, y + dy, z + dz).getType().isAir()) {
                                world.getBlockAt(x + dx, y + dy, z + dz).setType(org.bukkit.Material.FIRE, true);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void endEvent() {
        super.endEvent();
        // Remove all fire
        for(int x = ARENA_MIN_X; x < ARENA_MAX_X; x++) {
            for(int y = ARENA_MIN_Y; y < ARENA_MAX_Y; y++) {
                for (int z = ARENA_MIN_Z; z < ARENA_MAX_Z; z++) {
                    if (world.getBlockAt(x, y, z).getType() == org.bukkit.Material.FIRE) {
                        world.getBlockAt(x, y, z).setType(org.bukkit.Material.AIR);
                    }
                }
            }
        }
    }
}
