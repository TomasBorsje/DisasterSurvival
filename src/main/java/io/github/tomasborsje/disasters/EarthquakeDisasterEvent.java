package io.github.tomasborsje.disasters;

import io.github.tomasborsje.core.DisasterEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * An event where dangerous blocks collapse and fall down, and streaks of blocks are flung into the air.
 */
public class EarthquakeDisasterEvent extends DisasterEvent {
    private static final int FALLING_BLOCKS_PER_SECOND = 20;
    private static final int SOFT_FLUNG_BLOCKS_PER_SECOND = 20;
    private static final int TICKS_PER_BIG_FLING = 27;
    private static final double FLUNG_BLOCK_Y_VELOCITY = 1.5;
    private static final double FLUNG_BLOCK_HORIZONTAL_VELOCITY = 1.5;
    private static final double SOFT_FLING_VELOCITY = 1;
    private static final int FLING_SPHERE_RADIUS = 3;

    public EarthquakeDisasterEvent(World world) {
        super(world, "Earth Quake!", "Watch out for falling blocks!");
    }

    public void startEvent() {
        super.startEvent();
        // Play Pigstep sound to all player
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player, Sound.MUSIC_DISC_PIGSTEP, 1, 1);
        }
    }

    @Override
    public void tickEvent() {
        super.tickEvent();
        // Every 20 ticks, randomly replace blocks with falling blocks
        if(ticksRemaining % 20 == 0) {
            for(int i = 0; i < FALLING_BLOCKS_PER_SECOND; i++) {
                int x = (int) (Math.random() * 50);
                int z = (int) (Math.random() * 50);
                int y = ARENA_MAX_Y;

                // Go down from y=50 until we find a solid block to drop
                Block targetBlock = world.getBlockAt(x, y, z);
                while((targetBlock.getType().isAir() || targetBlock.getType() == Material.BARRIER) && y > ARENA_MIN_Y) {
                    y--;
                    targetBlock = world.getBlockAt(x, y, z);
                }
                // If coordinate is at the bottom of the world, skip because no block found
                if(y == ARENA_MIN_Y) {
                    continue;
                }
                // If solid block and there is air below it, replace with falling block
                if(!targetBlock.getType().isAir() && world.getBlockAt(x, y - 1, z).getType().isAir()) {
                    dropBlock(targetBlock);
                }
            }
        }
        // Every 20 ticks, randomly softly fling blocks
        if(ticksRemaining % 20 == 0) {
            for(int i = 0; i < SOFT_FLUNG_BLOCKS_PER_SECOND; i++) {
                int x = (int) (Math.random() * 50);
                int z = (int) (Math.random() * 50);
                int y = ARENA_MAX_Y;

                // Go down from y=50 until we find a solid block to fling
                Block targetBlock = world.getBlockAt(x, y, z);
                while((targetBlock.getType().isAir() || targetBlock.getType() == Material.BARRIER) && y > ARENA_MIN_Y) {
                    y--;
                    targetBlock = world.getBlockAt(x, y, z);
                }
                // If coordinate is at the bottom of the world, skip because no block found
                if(y == ARENA_MIN_Y) {
                    continue;
                }
                // Fling block with low velocity
                flingBlock(targetBlock, SOFT_FLING_VELOCITY);
            }
        }
        // Every set ticks, randomly fling a sphere of blocks into the air
        if(ticksRemaining % TICKS_PER_BIG_FLING == 0) {
            int x = (int) (Math.random() * 50);
            int z = (int) (Math.random() * 50);
            int y = ARENA_MAX_Y;
            // Clamp x and z to be within 4, 46 (so the edges don't reach outside the arena)
            x = Math.max(4, Math.min(46, x));
            z = Math.max(4, Math.min(46, z));

            // Go down from y=50 until we find a solid block to fling
            Block targetBlock = world.getBlockAt(x, y, z);
            while((targetBlock.getType().isAir() || targetBlock.getType() == Material.BARRIER) && y > ARENA_MIN_Y) {
                y--;
                targetBlock = world.getBlockAt(x, y, z);
            }
            // If coordinate is at the bottom of the world, skip because no block found
            if(y == ARENA_MIN_Y) {
                return;
            }

            // Play sound to all players
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(targetBlock.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 200, 0.6f);
            }
            // Get all blocks within a 5 radius sphere around the target block
            for(int i = -FLING_SPHERE_RADIUS; i <= FLING_SPHERE_RADIUS; i++) {
                for(int j = -2; j <= 2; j++) {
                    for(int k = -FLING_SPHERE_RADIUS; k <= FLING_SPHERE_RADIUS; k++) {
                        // If the block is within the sphere, fling it
                        if(i * i + j * j + k * k <= FLING_SPHERE_RADIUS * FLING_SPHERE_RADIUS) {
                            Block blockToFling = world.getBlockAt(x + i, y + j, z + k);
                            flingBlock(blockToFling, FLUNG_BLOCK_Y_VELOCITY);
                        }
                    }
                }
            }
        }
    }

    /**
     * Replaces the block at the given position with a falling block, making it fall.
     * @param block The block to drop
     */
    void dropBlock(Block block) {
        // If air, bedrock, or barrier, skip
        if(block.getType().isAir() || block.getType() == Material.BEDROCK || block.getType() == Material.BARRIER) {
            return;
        }
        Location loc = block.getLocation();
        // Delete block at position and spawn a burst of particles around the position
        world.spawnParticle(Particle.EXPLOSION, loc, 10, 0.1, 0.1, 0.1, 0.1);
        // Spawn falling block
        FallingBlock fallingBlock = world.spawnFallingBlock(loc, block.getBlockData());
        fallingBlock.setDamagePerBlock(2);
        fallingBlock.setMaxDamage(8);
        fallingBlock.setDropItem(false);
        fallingBlock.setHurtEntities(true);
        // Delete original block
        block.setType(Material.AIR);
    }

    /**
     * Flings a block into the air a slight horizontal variation.
     * @param block The block to fling
     */
    void flingBlock(Block block, double yVelocity) {
        // If air, bedrock, or barrier, skip
        if(block.getType().isAir() || block.getType() == Material.BEDROCK || block.getType() == Material.BARRIER) {
            return;
        }
        Location loc = block.getLocation();
        // Delete block at position and spawn a burst of particles around the position
        world.spawnParticle(Particle.EXPLOSION, loc, 10, 0.1, 0.1, 0.1, 0.1);
        // Spawn falling block
        FallingBlock flungBlock = world.spawnFallingBlock(loc, block.getBlockData());
        flungBlock.setDamagePerBlock(2);
        flungBlock.setMaxDamage(8);
        flungBlock.setDropItem(false);
        flungBlock.setHurtEntities(true);
        // Set falling block velocity to aim straight up with a small horizontal component
        flungBlock.setVelocity(new Vector(FLUNG_BLOCK_HORIZONTAL_VELOCITY * (Math.random() - 0.5), yVelocity, FLUNG_BLOCK_HORIZONTAL_VELOCITY * (Math.random() - 0.5)));
        // Delete original block
        block.setType(Material.AIR);
    }

    @Override
    public void endEvent() {
        super.endEvent();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.stopSound(Sound.MUSIC_DISC_PIGSTEP);
        }
    }
}
