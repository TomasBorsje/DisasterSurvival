package io.github.tomasborsje.disasters;

import io.github.tomasborsje.core.DisasterEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;

public class FlashFloodDisasterEvent extends DisasterEvent {
    public FlashFloodDisasterEvent(World world) {
        super(world, "Flash Flood", "Get to high ground!");
    }
    private static final int FLOOD_LEVEL_DELAY_TICKS = 50;
    private static final int FLOOD_DAMAGE_TICKS = 5;
    private static final int FLOOD_MAX_Y = 20;
    private int floodY = ARENA_MIN_Y;
    public void startEvent() {
        super.startEvent();
        // Set weather to raining
        world.setStorm(true);
    }
    @Override
    public void tickEvent() {
        super.tickEvent();
        // Every 5 seconds, raise the flood level by 1
        if(ticksRemaining % FLOOD_LEVEL_DELAY_TICKS == 0) {
            floodY++;
            Bukkit.getLogger().info("Raising flood to y="+floodY);
            // Play water bucket sound to all players
            for (org.bukkit.entity.Player player : world.getPlayers()) {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 0.3f);
            }
            // Fill all blocks at the flood level with water
            for(int x = ARENA_MIN_X; x < ARENA_MAX_X; x++) {
                for(int z = ARENA_MIN_Z; z < ARENA_MAX_Z; z++) {
                    // If air or water loggable, place water
                    Block block = world.getBlockAt(x, floodY, z);
                    if(block.getType().isAir()) {
                        block.setType(org.bukkit.Material.WATER);
                    }
                    else if(block.getBlockData() instanceof Waterlogged waterloggedData) {
                        waterloggedData.setWaterlogged(true);
                    }
                }
            }
        }

        // Every 20 ticks, if a player is in water, damage them
        if(ticksRemaining % FLOOD_DAMAGE_TICKS == 0) {
            for (org.bukkit.entity.Player player : world.getPlayers()) {
                Block block = player.getLocation().getBlock();
                if (block.getType() == org.bukkit.Material.WATER) {
                    player.damage(1);
                } else if (block.getBlockData() instanceof Waterlogged waterloggedData) {
                    if (waterloggedData.isWaterlogged()) {
                        player.damage(1);
                    }
                }
            }
        }
    }

    @Override
    public void endEvent() {
        super.endEvent();
        // Set weather to clear
        world.setStorm(false);
        // Remove all water
        for(int x = ARENA_MIN_X; x < ARENA_MAX_X; x++) {
            for(int y = ARENA_MIN_Y; y < ARENA_MAX_Y; y++) {
                for (int z = ARENA_MIN_Z; z < ARENA_MAX_Z; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if(block.getType() == org.bukkit.Material.WATER) {
                        block.setType(org.bukkit.Material.AIR);
                    }
                    else if(block.getBlockData() instanceof Waterlogged waterloggedData) {
                        waterloggedData.setWaterlogged(false);
                    }
                }
            }
        }
    }
}
